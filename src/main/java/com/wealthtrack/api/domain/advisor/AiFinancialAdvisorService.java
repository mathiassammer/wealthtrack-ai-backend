package com.wealthtrack.api.domain.advisor;

import com.wealthtrack.api.domain.transaction.model.Transaction;
import com.wealthtrack.api.domain.transaction.repository.TransactionRepository;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AiFinancialAdvisorService {

    private final ChatClient chatClient;
    private final TransactionRepository transactionRepository;

    // O Spring AI 1.0+ recomenda injetar o ChatClient.Builder
    public AiFinancialAdvisorService(ChatClient.Builder chatClientBuilder, TransactionRepository transactionRepository) {
        // Configuramos o "System Prompt" (A Regra Mestra) logo na construção do cliente
        this.chatClient = chatClientBuilder
                .defaultSystem("Você é um consultor financeiro do aplicativo WealthTrack AI. " +
                        "Sua missão é analisar as transações financeiras fornecidas no contexto e responder à pergunta do usuário. " +
                        "NUNCA invente valores, dados ou transações. Baseie-se ESTRITAMENTE nos dados fornecidos. " +
                        "Seja encorajador, mas alerte sobre gastos excessivos se notar.")
                .build();
        this.transactionRepository = transactionRepository;
    }

    public String askAdvisor(UUID accountId, String userQuestion) {

        // 1. RETRIEVAL (A Busca / O "R" do RAG)
        // Vamos buscar as transações reais da conta do usuário no nosso banco de dados.
        // Em um sistema real, você filtraria por data (ex: últimos 30 dias) para não enviar 10 anos de dados.
        List<Transaction> recentTransactions = transactionRepository.findByAccountId(accountId);

        // 2. FORMATAÇÃO DO CONTEXTO
        // Transformamos a lista de objetos Java em um texto limpo para a IA entender.
        String contextData = recentTransactions.stream()
                .map(t -> String.format("Data: %s | Tipo: %s | Categoria: %s | Valor: R$ %.2f | Descrição: %s",
                        t.getTransactionDate(), t.getType(), t.getCategory(), t.getAmount(), t.getDescription()))
                .collect(Collectors.joining("\n"));

        // 3. AUGMENTED GENERATION (A Montagem da Pergunta + Contexto / O "A" e "G" do RAG)
        // Usamos um template para injetar as transações junto com a pergunta do usuário
        String promptText = """
                Aqui está o meu histórico recente de transações:
                {context}
                
                Com base exclusivamente nos dados acima, responda à minha pergunta:
                {question}
                """;

        PromptTemplate template = new PromptTemplate(promptText);
        template.add("context", contextData.isEmpty() ? "Nenhuma transação encontrada." : contextData);
        template.add("question", userQuestion);

        // 4. CHAMADA PARA A INTELIGÊNCIA ARTIFICIAL
        // O chatClient envia o pacote completo (System Prompt + Histórico do Banco + Pergunta) e nos devolve a resposta.
        return this.chatClient.prompt(template.create())
                .call()
                .content();
    }
}