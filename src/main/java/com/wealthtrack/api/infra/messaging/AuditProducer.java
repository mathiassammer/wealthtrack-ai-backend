package com.wealthtrack.api.infra.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class AuditProducer {

    private static final String TOPIC = "transaction_audit_log";

    // O KafkaTemplate é a classe do Spring que fala com o servidor do Kafka
    private final KafkaTemplate<String, String> kafkaTemplate;
    // O ObjectMapper do Jackson converte nosso Objeto Java para JSON
    private final ObjectMapper objectMapper;

    public AuditProducer(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public void sendAuditLog(Object eventData) {
        try {
            // Transforma o objeto do evento em uma String JSON
            String jsonPayload = objectMapper.writeValueAsString(eventData);

            // Joga no diário indestrutível do Kafka (Tópico, Chave, Valor)
            kafkaTemplate.send(TOPIC, jsonPayload);

            System.out.println("Evento enviado ao Kafka: " + jsonPayload);
        } catch (Exception e) {
            // Em produção, se o Kafka falhar, nós logamos o erro, mas NÃO travamos o sistema!
            System.err.println("Erro ao enviar para o Kafka: " + e.getMessage());
        }
    }
}