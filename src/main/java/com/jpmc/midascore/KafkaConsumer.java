
package com.jpmc.midascore;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jpmc.midascore.entity.Incentive;
import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.repository.TransactionRecordRepository;
import com.jpmc.midascore.repository.UserRecordRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import com.jpmc.midascore.foundation.Transaction; // adjust package if necessary
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
public class KafkaConsumer {

    @Autowired
    private UserRecordRepository userRepository;

    @Autowired
    private TransactionRecordRepository transactionRepository;

    @Autowired
    private RestTemplate restTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @KafkaListener(topics = "${general.kafka-topic}", groupId = "midas-core")
    @Transactional
    public void consume(String transactionLine) {
        try {
            // Deserialize the JSON message into a Transaction object - Incoming message
            Transaction transaction = objectMapper.readValue(transactionLine, Transaction.class);
            System.out.println("----------------------------------------");
            System.out.println("Received transaction: " + transaction);
            System.out.println("----------------------------------------");

              // Fetch sender and recipient from DB
//            UserRecord sender = userRepository.findById(transaction.getSenderId()).orElse(null);
//            UserRecord recipient = userRepository.findById(transaction.getRecipientId()).orElse(null);

            Optional<UserRecord> senderOpt = userRepository.findById(transaction.getSenderId());
            Optional<UserRecord> recipientOpt = userRepository.findById(transaction.getRecipientId());

//            Optional<UserRecord> senderOpt = Optional.ofNullable(userRepository.findById(transaction.getSenderId()));
//            Optional<UserRecord> recipientOpt = Optional.ofNullable(userRepository.findById(transaction.getRecipientId()));

            UserRecord sender = senderOpt.orElse(null);
            UserRecord recipient = recipientOpt.orElse(null);

            // Validate transaction
            if (sender != null && recipient != null && sender.getBalance() >= transaction.getAmount()) {

                    // If Valid Transaction then
                    // ✅ Call Incentive API before saving
                    // USE of External REST API is to calculate the incentive by taking (POST) transaction class
                    Incentive incentive = restTemplate.postForObject(
                            "http://localhost:8080/incentive",
                            transaction,
                            Incentive.class
                    );

                    float incentiveAmount = incentive != null ? incentive.getAmount() : 0f;



                // Task 3
                // Adjust balances
                // Subtract the transaction amount from sender and Add the same transaction amount to the recipient
//                sender.setBalance(sender.getBalance() - transaction.getAmount());
//                recipient.setBalance(recipient.getBalance() + transaction.getAmount());

                // Task 4 - Incentive API Integration
                sender.setBalance(sender.getBalance() - transaction.getAmount());
                recipient.setBalance(recipient.getBalance() + transaction.getAmount() + incentiveAmount);

                // Create and save transaction record
                TransactionRecord record = new TransactionRecord(sender, recipient, transaction.getAmount(), incentiveAmount);
                transactionRepository.save(record);

                // Save updated users
                userRepository.save(sender);
                userRepository.save(recipient);

                System.out.println("Transaction recorded: " + record);
                System.out.println("*********************");
                System.out.println("Transaction recorded with incentive: " + incentiveAmount);
            } else {
                System.out.println("Invalid transaction, discarded: " + transaction);
            }

                // TASK - 3
                userRepository.findByName("waldorf").ifPresent(waldorf -> System.out.println("Final balance of waldorf: " + waldorf.getBalance()));

//            UserRecord waldorf = userRepository.findByName("waldorf").orElse(null);
//            if (waldorf != null) {
//                float balance = waldorf.getBalance();
//            }

            // TASK - 4
            System.out.println("Final balance of wilbur: " +
                    userRepository.findByName("wilbur").map(UserRecord::getBalance).orElse(0f));



        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

















