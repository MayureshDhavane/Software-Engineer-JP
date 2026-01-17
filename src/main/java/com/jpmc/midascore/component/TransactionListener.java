package com.jpmc.midascore.component;

import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.service.TransactionService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class TransactionListener {


    private final TransactionService transactionService;

    public TransactionListener(TransactionService transactionService) {
        this.transactionService = transactionService;
    }
    /**
     * This method is invoked automatically whenever a message
     * arrives at the configured Kafka topic.
     */
    @KafkaListener(
            topics = "${general.kafka-topic}",
            groupId = "midas-core-consumer"
    )
    public void consume(Transaction transaction) {
        transactionService.process(transaction);
    }
}