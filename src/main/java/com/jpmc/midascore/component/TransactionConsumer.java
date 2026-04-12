package com.jpmc.midascore.component;

import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.repository.TransactionRepository;
import com.jpmc.midascore.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class TransactionConsumer {
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    @Autowired
    public TransactionConsumer(TransactionRepository transactionRepository, UserRepository userRepository) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository; // Fixed: Added this to the constructor
    }

    @KafkaListener(topics = "${general.kafka-topic}", 
                   groupId = "midas-core-group", 
                   containerFactory = "kafkaListenerContainerFactory")
    public void listen(Transaction transaction) {
        // 1. Fetch users from database
        UserRecord sender = userRepository.findById(transaction.getSenderId());
        UserRecord recipient = userRepository.findById(transaction.getRecipientId());
        
        // 2. Validate transaction
        if (sender != null && recipient != null && sender.getBalance() >= transaction.getAmount()) {

            // 3. Update balances
            sender.setBalance(sender.getBalance() - transaction.getAmount());
            recipient.setBalance(recipient.getBalance() + transaction.getAmount());

            // 4. Save everything
            userRepository.save(sender);
            userRepository.save(recipient);

            TransactionRecord record = new TransactionRecord(sender, recipient, transaction.getAmount());
            transactionRepository.save(record);

            System.out.println("Processed: " + transaction);
        }

        
        };
    }
