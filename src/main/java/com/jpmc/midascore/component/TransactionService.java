package com.jpmc.midascore.component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Incentive;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.repository.TransactionRecordRepository;
import com.jpmc.midascore.repository.UserRepository;

@Service
public class TransactionService {
    
    private static final Logger log = LoggerFactory.getLogger(TransactionService.class);
    private static final String INCENTIVE_API_URL = "http://localhost:8080/incentive";
    
    private final UserRepository userRepository;
    private final TransactionRecordRepository transactionRecordRepository;
    private final RestTemplate restTemplate;

    public TransactionService(UserRepository userRepository, 
                            TransactionRecordRepository transactionRecordRepository,
                            RestTemplate restTemplate) {
        this.userRepository = userRepository;
        this.transactionRecordRepository = transactionRecordRepository;
        this.restTemplate = restTemplate;
    }

    @Transactional
    public void processTransaction(Transaction transaction) {
        // Validate sender exists
        UserRecord sender = userRepository.findById(transaction.getSenderId());
        if (sender == null) {
            log.warn("Invalid transaction: sender ID {} not found", transaction.getSenderId());
            return;
        }

        // Validate recipient exists
        UserRecord recipient = userRepository.findById(transaction.getRecipientId());
        if (recipient == null) {
            log.warn("Invalid transaction: recipient ID {} not found", transaction.getRecipientId());
            return;
        }

        // Validate sender has sufficient balance
        if (sender.getBalance() < transaction.getAmount()) {
            log.warn("Invalid transaction: sender {} has insufficient balance. Balance: {}, Amount: {}", 
                    sender.getName(), sender.getBalance(), transaction.getAmount());
            return;
        }

        // Transaction is valid - get incentive from API
        Incentive incentive = getIncentive(transaction);
        float incentiveAmount = (incentive != null) ? incentive.getAmount() : 0.0f;
        
        log.info("Processing valid transaction: {} -> {} amount: {} incentive: {}", 
                sender.getName(), recipient.getName(), transaction.getAmount(), incentiveAmount);

        // Update balances
        // Sender: subtract transaction amount only (NOT the incentive)
        sender.setBalance(sender.getBalance() - transaction.getAmount());
        
        // Recipient: add transaction amount + incentive
        recipient.setBalance(recipient.getBalance() + transaction.getAmount() + incentiveAmount);

        // Save updated users
        userRepository.save(sender);
        userRepository.save(recipient);

        // Create and save transaction record with incentive
        TransactionRecord record = new TransactionRecord(sender, recipient, transaction.getAmount(), incentiveAmount);
        transactionRecordRepository.save(record);

        log.info("Transaction recorded successfully. New balances - {}: {}, {}: {}", 
                sender.getName(), sender.getBalance(), 
                recipient.getName(), recipient.getBalance());
    }

    private Incentive getIncentive(Transaction transaction) {
        try {
            // POST transaction to incentive API
            Incentive incentive = restTemplate.postForObject(
                INCENTIVE_API_URL, 
                transaction, 
                Incentive.class
            );
            log.debug("Received incentive: {}", incentive);
            return incentive;
        } catch (Exception e) {
            log.error("Failed to get incentive from API: {}", e.getMessage());
            return new Incentive(0.0f);
        }
    }
}