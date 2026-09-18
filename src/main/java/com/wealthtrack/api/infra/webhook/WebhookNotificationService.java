package com.wealthtrack.api.infra.webhook;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class WebhookNotificationService {

    // A URL que o N8N vai nos dar quando criarmos um nó "Webhook" lá na interface dele
    // Colocaremos isso no nosso arquivo application.properties / .env
    @Value("${n8n.webhook.alert.url}")
    private String n8nWebhookUrl;

    // Classe nativa do Spring para fazer chamadas HTTP (como o Postman faz)
    private final RestTemplate restTemplate;

    public WebhookNotificationService() {
        this.restTemplate = new RestTemplate();
    }

    public void sendHighValueExpenseAlert(String userEmail, String category, String amount) {
        try {
            // 1. Montamos o "pacote" de dados (Payload JSON) que enviaremos para o N8N
            Map<String, String> payload = new HashMap<>();
            payload.put("email", userEmail);
            payload.put("category", category);
            payload.put("amount", amount);
            payload.put("message", "Alerta: Uma transação de alto valor foi registrada!");

            // 2. Avisamos que estamos enviando um JSON
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // 3. Juntamos o cabeçalho e os dados
            HttpEntity<Map<String, String>> request = new HttpEntity<>(payload, headers);

            // 4. Disparamos o "Sinal" (POST) para a URL do N8N.
            // O Java não sabe e não liga se isso vai virar um E-mail ou Telegram. Ele fez a parte dele.
            restTemplate.postForEntity(n8nWebhookUrl, request, String.class);

            System.out.println("Sinal de alerta enviado para o N8N com sucesso.");

        } catch (Exception e) {
            // Regra de resiliência: O sistema não deve travar se o N8N estiver fora do ar.
            System.err.println("Falha ao contactar o N8N: " + e.getMessage());
        }
    }
}