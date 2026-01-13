package com.jpmc.midascore;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jpmc.midascore.foundation.Transaction;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${general.kafka-topic}")
    private String topic;

    public KafkaProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void send(String transactionLine) {
        try {
            // transactionLine example: "1, 2, 134.50"
            String[] parts = transactionLine.split(",\\s*");
            Transaction transaction = new Transaction(
                    Long.parseLong(parts[0]),
                    Long.parseLong(parts[1]),
                    Float.parseFloat(parts[2])
            );

            // Serialize Transaction object to JSON and send
            String json = objectMapper.writeValueAsString(transaction);
            kafkaTemplate.send(topic, json);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}