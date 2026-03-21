package com.jpmc.midascore;

import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Incentive;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.repository.TransactionRecordRepository;
import com.jpmc.midascore.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

@Service
public class TransactionService {
    private static final Logger logger = LoggerFactory.getLogger(TransactionService.class);

    private final UserRepository userRepository;
    private final TransactionRecordRepository transactionRecordRepository;
    private final RestTemplate restTemplate;
    private final String incentiveApiUrl;

    public TransactionService(UserRepository userRepository,
                              TransactionRecordRepository transactionRecordRepository,
                              RestTemplateBuilder restTemplateBuilder,
                              @Value("${incentive.api.url:http://localhost:8080/incentive}") String incentiveApiUrl) {
        this.userRepository = userRepository;
        this.transactionRecordRepository = transactionRecordRepository;
        this.restTemplate = restTemplateBuilder.build();
        this.incentiveApiUrl = incentiveApiUrl;
    }

    @Transactional
    public void applyTransaction(Transaction tx) {
        if (tx == null) {
            return;
        }

        UserRecord sender = userRepository.findById(tx.getSenderId()).orElse(null);
        UserRecord recipient = userRepository.findById(tx.getRecipientId()).orElse(null);

        if (sender == null || recipient == null) {
            logger.warn("Skipping transaction due to missing user(s): {}", tx);
            return;
        }

        if (sender.getBalance() < tx.getAmount()) {
            logger.warn("Skipping transaction due to insufficient balance: {}", tx);
            return;
        }

        Incentive incentive = new Incentive(0f);
        try {
            incentive = restTemplate.postForObject(incentiveApiUrl, tx, Incentive.class);
            if (incentive == null) {
                incentive = new Incentive(0f);
            }
        } catch (Exception e) {
            logger.warn("Error calling incentive API; proceeding with 0 incentive for tx {}: {}", tx, e.getMessage());
            incentive = new Incentive(0f);
        }

        float incentiveAmount = incentive.getAmount();
        if (incentiveAmount < 0) {
            incentiveAmount = 0f;
        }

        sender.setBalance(sender.getBalance() - tx.getAmount());
        recipient.setBalance(recipient.getBalance() + tx.getAmount() + incentiveAmount);

        userRepository.save(sender);
        userRepository.save(recipient);

        TransactionRecord record = new TransactionRecord(sender, recipient, tx.getAmount(), incentiveAmount);
        transactionRecordRepository.save(record);

        logger.info("Applied transaction: {} | incentive: {} | sender after: {} recipient after: {}",
                tx, incentiveAmount, sender.getBalance(), recipient.getBalance());
    }
}
