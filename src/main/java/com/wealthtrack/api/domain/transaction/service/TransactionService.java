package com.wealthtrack.api.domain.transaction.service;

import com.wealthtrack.api.domain.account.model.Account;
import com.wealthtrack.api.domain.account.repository.AccountRepository;
import com.wealthtrack.api.domain.transaction.dto.TransactionRequestDTO;
import com.wealthtrack.api.domain.transaction.model.Transaction;
import com.wealthtrack.api.domain.transaction.model.TransactionType;
import com.wealthtrack.api.domain.transaction.repository.TransactionRepository;
import com.wealthtrack.api.infra.messaging.AuditProducer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class TransactionService {

    // Injeção de dependências via construtor (Melhor prática do SOLID ao invés de usar @Autowired)
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final AuditProducer auditProducer;

    public TransactionService(TransactionRepository transactionRepository, AccountRepository accountRepository, AuditProducer auditProducer) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.auditProducer = auditProducer;
    }

    // A MÁGICA: @Transactional garante que, se o sistema der erro na linha 35,
    // o desconto do saldo feito na linha 33 é CANCELADO (Rollback) automaticamente no banco!
    @Transactional
    public Transaction registerTransaction(TransactionRequestDTO dto) {

        // 1. Busca a conta no banco (Falha rápido se não existir)
        Account account = accountRepository.findById(dto.accountId())
                .orElseThrow(() -> new IllegalArgumentException("Conta bancária não encontrada."));

        // 2. Cria a nova transação
        Transaction transaction = new Transaction(
                account, dto.amount(), dto.type(), dto.category(), dto.description(), dto.date()
        );

        // 3. Regra de Negócio: Atualiza o saldo da conta
        if (dto.type() == TransactionType.EXPENSE) {
            account.subtractBalance(dto.amount()); // Chama o método da própria Conta (Encapsulamento)
        } else if (dto.type() == TransactionType.INCOME) {
            account.addBalance(dto.amount());
        }

        // 4. Salva as alterações no banco de dados
        accountRepository.save(account);

        Transaction savedTransaction = transactionRepository.save(transaction);

        // A MÁGICA DA MENSAGERIA: O sistema bancário foi atualizado.
        // Agora, nós "jogamos a carta" no Kafka de forma assíncrona.
        // O sistema de IA (ou qualquer outro) lerá isso no futuro para aprender seu perfil de gastos.
        auditProducer.sendAuditLog(savedTransaction);

        return savedTransaction;
    }
}
