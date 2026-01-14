package com.jpmc.midascore.component;

import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Incentive;
import com.jpmc.midascore.foundation.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Component
public class TransactionListener {
    private static final Logger logger = LoggerFactory.getLogger(TransactionListener.class);
    private final List<Transaction> receivedTransactions = new ArrayList<>();
    private final DatabaseConduit databaseConduit;
    private final RestTemplate restTemplate;
    private static final String INCENTIVE_API_URL = "http://localhost:8080/incentive";

    public TransactionListener(DatabaseConduit databaseConduit, RestTemplate restTemplate) {
        this.databaseConduit = databaseConduit;
        this.restTemplate = restTemplate;
    }

    @KafkaListener(topics = "${general.kafka-topic}", groupId = "midas-core-group")
    @Transactional
    public void listen(Transaction transaction) {
        receivedTransactions.add(transaction);
        logger.info("Received transaction #{}: {}", receivedTransactions.size(), transaction);

        // Validate and process the transaction
        processTransaction(transaction);
    }

    private void processTransaction(Transaction transaction) {
        // Validate sender exists
        UserRecord sender = databaseConduit.findUserById(transaction.getSenderId());
        if (sender == null) {
            logger.warn("Invalid transaction: Sender {} not found", transaction.getSenderId());
            return;
        }

        // Validate recipient exists
        UserRecord recipient = databaseConduit.findUserById(transaction.getRecipientId());
        if (recipient == null) {
            logger.warn("Invalid transaction: Recipient {} not found", transaction.getRecipientId());
            return;
        }

        // Validate sender has sufficient balance
        if (sender.getBalance() < transaction.getAmount()) {
            logger.warn("Invalid transaction: Sender {} has insufficient balance. Balance: {}, Amount: {}",
                    sender.getName(), sender.getBalance(), transaction.getAmount());
            return;
        }

        // Transaction is valid - call incentive API
        Incentive incentive = restTemplate.postForObject(INCENTIVE_API_URL, transaction, Incentive.class);
        float incentiveAmount = (incentive != null) ? incentive.getAmount() : 0f;

        // Update balances: deduct from sender, add amount + incentive to recipient
        sender.setBalance(sender.getBalance() - transaction.getAmount());
        recipient.setBalance(recipient.getBalance() + transaction.getAmount() + incentiveAmount);

        // Save updated users
        databaseConduit.save(sender);
        databaseConduit.save(recipient);

        // Save transaction record with incentive
        TransactionRecord record = new TransactionRecord(sender, recipient, transaction.getAmount(), incentiveAmount);
        databaseConduit.saveTransaction(record);

        logger.info("Transaction processed successfully: {} sent {} to {} (incentive: {})",
                sender.getName(), transaction.getAmount(), recipient.getName(), incentiveAmount);
    }

    public List<Transaction> getReceivedTransactions() {
        return receivedTransactions;
    }
}
