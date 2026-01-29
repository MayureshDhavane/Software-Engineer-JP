package com.jpmc.midascore.component;

import com.jpmc.midascore.foundation.Transaction;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class TransactionReceiver {

    private final DatabaseConduit databaseConduit;

    public TransactionReceiver(DatabaseConduit databaseConduit) {
        this.databaseConduit = databaseConduit;
    }

    @KafkaListener(topics = "${general.kafka-topic}", groupId = "midas-group", autoStartup = "#{environment.containsProperty('spring.embedded.kafka.brokers')}")
    public void receive(Transaction transaction) {
        databaseConduit.processTransaction(transaction);
    }
}
