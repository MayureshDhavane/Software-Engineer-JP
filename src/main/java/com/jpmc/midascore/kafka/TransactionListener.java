package com.jpmc.midascore.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.jpmc.midascore.component.TransactionService;
import com.jpmc.midascore.foundation.Transaction;

@Component
public class TransactionListener {
    
    private static final Logger log = LoggerFactory.getLogger(TransactionListener.class);
    private final TransactionService transactionService;
    
    private int count = 0;

    public TransactionListener(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @KafkaListener(topics = "${general.kafka-topic}", groupId = "midas-group")
    public void listen(Transaction transaction) {
        count++;
        log.info("=== RECEIVED TRANSACTION #{} - Amount: {} ===", count, transaction.getAmount());
        
        // Process the transaction (validate and save to database)
        transactionService.processTransaction(transaction);
    }
}
