package com.jpmc.midascore.component;

import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.foundation.IncentiveResponse;  // IMPORT ADD
import com.jpmc.midascore.repository.TransactionRecordRepository;
import com.jpmc.midascore.repository.UserRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;

@Component
public class TransactionListener {
    private final UserRepository userRepository;
    private final TransactionRecordRepository transactionRecordRepository;
    private final RestTemplate restTemplate = new RestTemplate();
    private final String INCENTIVE_API_URL = "http://localhost:8080/incentive";

    public TransactionListener(UserRepository userRepository,
                               TransactionRecordRepository transactionRecordRepository) {
        this.userRepository = userRepository;
        this.transactionRecordRepository = transactionRecordRepository;
    }

    @KafkaListener(topics = "${general.kafka-topic}")
    @Transactional
    public void receiveTransaction(Transaction transaction) {
        // 1. Find sender and recipient
        UserRecord sender = userRepository.findById(transaction.getSenderId());
        UserRecord recipient = userRepository.findById(transaction.getRecipientId());

        // 2. Validate
        if (sender == null || recipient == null) {
            System.out.println("Invalid sender or recipient ID, discarding transaction: " + transaction);
            return;
        }
        if (sender.getBalance() < transaction.getAmount()) {
            System.out.println("Insufficient balance, discarding transaction: " + transaction);
            return;
        }

        // 3. CALL INCENTIVE API
        float incentiveAmount = 0.0f;
        try {
            IncentiveResponse incentiveResponse = restTemplate.postForObject(
                    INCENTIVE_API_URL,
                    transaction,
                    IncentiveResponse.class
            );
            if (incentiveResponse != null) {
                incentiveAmount = incentiveResponse.getAmount();
            }
        } catch (Exception e) {
            System.out.println("Incentive API call failed, using 0 incentive: " + e.getMessage());
        }

        // 4. Update balances
        float amount = transaction.getAmount();

        // Sender: deduct only transaction amount (NO incentive deduction)
        sender.setBalance(sender.getBalance() - amount);

        // Recipient: add transaction amount + incentive
        recipient.setBalance(recipient.getBalance() + amount + incentiveAmount);

        // 5. Save updated users
        userRepository.save(sender);
        userRepository.save(recipient);

        // 6. Create and save transaction record WITH INCENTIVE
        TransactionRecord record = new TransactionRecord(amount, sender, recipient, LocalDateTime.now());
        record.setIncentive(incentiveAmount);
        transactionRecordRepository.save(record);

        System.out.println("Processed transaction with incentive: " + incentiveAmount);
    }
}