package com.jpmc.midascore.component;

import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Incentive;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.repository.TransactionRepository;
import com.jpmc.midascore.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class TransactionListener {
    private static final Logger logger = LoggerFactory.getLogger(TransactionListener.class);

    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final IncentiveRequester incentiveRequester;

    public TransactionListener(UserRepository userRepository, TransactionRepository transactionRepository, IncentiveRequester incentiveRequester) {
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
        this.incentiveRequester = incentiveRequester;
    }

    @KafkaListener(topics = "${general.kafka-topic}", groupId = "midas-core")
    @Transactional
    public void listen(Transaction transaction) {
        logger.info("Received transaction: {}", transaction);

        UserRecord sender = userRepository.findById(transaction.getSenderId());
        UserRecord recipient = userRepository.findById(transaction.getRecipientId());

        if (isValid(sender, recipient, transaction)) {
            processTransaction(sender, recipient, transaction);
        } else {
            logger.warn("Transaction invalid/discarded: {}", transaction);
        }
    }

    private boolean isValid(UserRecord sender, UserRecord recipient, Transaction transaction) {
        if (sender == null || recipient == null) {
            return false;
        }
        if (sender.getBalance() < transaction.getAmount()) {
            return false;
        }
        return true;
    }

    private void processTransaction(UserRecord sender, UserRecord recipient, Transaction transaction) {
        float amount = transaction.getAmount();
        float incentiveAmount = 0;
        
        try {
            Incentive incentive = incentiveRequester.getIncentive(transaction);
            incentiveAmount = incentive.getAmount();
        } catch (Exception e) {
            logger.error("Error fetching incentive", e);
        }

        // Update balances
        sender.setBalance(sender.getBalance() - amount);
        recipient.setBalance(recipient.getBalance() + amount + incentiveAmount);

        // Save users
        userRepository.save(sender);
        userRepository.save(recipient);

        // Record transaction
        TransactionRecord record = new TransactionRecord(sender, recipient, amount, incentiveAmount);
        transactionRepository.save(record);

        logger.info("Processed transaction: {}", record);
    }
}
