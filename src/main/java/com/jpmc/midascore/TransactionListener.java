package com.jpmc.midascore;

import com.jpmc.midascore.component.IncentiveApiService;
import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Incentive;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.repository.TransactionRecordRepository;
import com.jpmc.midascore.repository.UserRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

@Component
public class TransactionListener {
    private static final Logger logger = LoggerFactory.getLogger(TransactionListener.class);
    
    private final UserRepository userRepository;
    private final TransactionRecordRepository transactionRecordRepository;
    private final IncentiveApiService incentiveApiService;

    public TransactionListener(UserRepository userRepository, 
                               TransactionRecordRepository transactionRecordRepository,
                               IncentiveApiService incentiveApiService) {
        this.userRepository = userRepository;
        this.transactionRecordRepository = transactionRecordRepository;
        this.incentiveApiService = incentiveApiService;
    }

    @KafkaListener(topics = "${general.kafka-topic}")
    @Transactional
    public void listen(String transactionLine) {
        try {
            // 1. Parse the incoming string
            String[] parts = transactionLine.split(", ");
            long senderId = Long.parseLong(parts[0]);
            long recipientId = Long.parseLong(parts[1]);
            float amount = Float.parseFloat(parts[2]);

            // 2. Fetch Users
            UserRecord sender = userRepository.findById(senderId);
            UserRecord recipient = userRepository.findById(recipientId);

            // 3. Validation
            if (sender == null || recipient == null) {
                logger.warn("Invalid transaction: Users not found");
                return;
            }
            if (sender.getBalance() < amount) {
                logger.warn("Sender {} has insufficient funds", senderId);
                return;
            }

            // 4. CALL THE INCENTIVE API
            Transaction transaction = new Transaction(senderId, recipientId, amount);
            Incentive incentive = incentiveApiService.getIncentive(transaction);
            float incentiveAmount = incentive.getAmount();

            // 5. Process Money
            // Sender loses ONLY the original amount
            sender.setBalance(sender.getBalance() - amount);
            
            // Recipient gets original amount + INCENTIVE
            recipient.setBalance(recipient.getBalance() + amount + incentiveAmount);

            // 6. Save Everything
            userRepository.save(sender);
            userRepository.save(recipient);

            TransactionRecord record = new TransactionRecord(sender, recipient, amount, incentiveAmount);
            transactionRecordRepository.save(record);
            
            logger.info("Success: {} sent {} to {} (Incentive: {})", senderId, amount, recipientId, incentiveAmount);

        } catch (Exception e) {
            logger.error("Error processing transaction", e);
        }
    }
}