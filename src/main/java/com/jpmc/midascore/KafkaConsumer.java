package com.jpmc.midascore;

import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.repository.UserRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaConsumer {
    private final UserRepository userRepository;

    public KafkaConsumer(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @KafkaListener(topics = "${general.kafka-topic:trader-updates}", groupId = "midas-core-group")
    public void listen(Transaction transaction) {
        // Task 2 Output
        System.out.println("RESULT_AMOUNT: " + transaction.getAmount());

        // Inside your listen method in KafkaConsumer.java:
        UserRecord sender = userRepository.findById(transaction.getSenderId()).orElse(null);
        UserRecord receiver = userRepository.findById(transaction.getRecipientId()).orElse(null); // Must be getRecipientId (with a 'p')

        if (sender != null && receiver != null && sender.getBalance() >= transaction.getAmount()) {
            sender.setBalance(sender.getBalance() - transaction.getAmount());
            receiver.setBalance(receiver.getBalance() + transaction.getAmount());

            userRepository.save(sender);
            userRepository.save(receiver);

            // Special Print for Task 3 Answer
            if (receiver.getName().equalsIgnoreCase("waldorf")) {
                System.out.println("WALDORF_BALANCE: " + receiver.getBalance());
            }
        }
    }
}