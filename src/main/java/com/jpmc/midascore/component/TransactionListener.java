package com.jpmc.midascore.component;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.entity.User;
import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.repository.UserRepository;
import com.jpmc.midascore.repository.TransactionRecordRepository;

import java.util.Optional;

@Component
public class TransactionListener {

    private final UserRepository userRepository;
    private final TransactionRecordRepository transactionRecordRepository;

    public TransactionListener(UserRepository userRepository,
                               TransactionRecordRepository transactionRecordRepository) {
        this.userRepository = userRepository;
        this.transactionRecordRepository = transactionRecordRepository;
    }

    @KafkaListener(topics = "${general.kafka-topic}")
    public void listen(Transaction transaction) {

        Optional<User> senderOpt = userRepository.findById(transaction.getSenderId());
        Optional<User> recipientOpt = userRepository.findById(transaction.getRecipientId());

        if(senderOpt.isPresent() && recipientOpt.isPresent()) {

            User sender = senderOpt.get();
            User recipient = recipientOpt.get();

            if(sender.getBalance() >= transaction.getAmount()) {

                sender.setBalance(sender.getBalance() - transaction.getAmount());
                recipient.setBalance(recipient.getBalance() + transaction.getAmount());

                userRepository.save(sender);
                userRepository.save(recipient);

                TransactionRecord record = new TransactionRecord();
                record.setAmount(transaction.getAmount());
                record.setSender(sender);
                record.setRecipient(recipient);

                transactionRecordRepository.save(record);
            }
        }
    }
}