package com.jpmc.midascore.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jpmc.midascore.foundation.Transaction;

@Component
public class KafkaConsumer {

    private final ObjectMapper mapper = new ObjectMapper();

    @KafkaListener(
        topics = "${general.kafka-topic}",
        groupId = "midas-core-group"
    )
    public void consume(String message) throws Exception {
        Transaction transaction =
                mapper.readValue(message, Transaction.class);

        System.out.println(transaction);
    }
}
