package com.jpmc.midascore;

import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.repository.TransactionRecordRepository;
import com.jpmc.midascore.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaConsumer {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionRecordRepository transactionRecordRepository;

    @Autowired
    private IncentiveClient incentiveClient;


    private final ObjectMapper objectMapper = new ObjectMapper();

    @KafkaListener(topics = "${general.kafka-topic}", groupId = "midas-core-group")
public void consume(String message) {
    try {
        System.out.println("Kafka message received: " + message);

        Transaction transaction =
                objectMapper.readValue(message, Transaction.class);

        UserRecord sender =
                userRepository.findById(transaction.getSenderId());
        UserRecord recipient =
                userRepository.findById(transaction.getRecipientId());

        if (sender == null || recipient == null)
            { System.out.println("RETURN: sender or recipient null");
             return;
}

        if (sender.getBalance() < transaction.getAmount()){
          System.out.println("RETURN: insufficient funds");
          return;
        }

        float incentive =
                incentiveClient.fetchIncentive(transaction).getAmount();

        TransactionRecord record =
                new TransactionRecord(sender, recipient, transaction.getAmount());
        record.setIncentive(incentive);
        transactionRecordRepository.save(record);

        sender.setBalance(sender.getBalance() - transaction.getAmount());
        recipient.setBalance(
                recipient.getBalance() + transaction.getAmount() + incentive);
              


        userRepository.save(sender);
        userRepository.save(recipient);

    } catch (Exception e) {
        e.printStackTrace();
    }
}
}