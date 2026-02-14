package com.jpmc.midascore.component;

import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.repository.TransactionRecordRepository;
import com.jpmc.midascore.repository.UserRepository;
//import jakarta.transaction.Transaction;
import com.jpmc.midascore.foundation.Transaction;
import jakarta.transaction.Transactional;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Optional;
@Component
public class TransactionListener {

    private final UserRepository userRepository;
    private final TransactionRecordRepository transactionRepository;
    private final IncentiveClient incentiveClient;

    public TransactionListener(
            UserRepository userRepository,
            TransactionRecordRepository transactionRepository,
            IncentiveClient incentiveClient
    ) {
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
        this.incentiveClient = incentiveClient;
    }

    @Transactional
    @KafkaListener(topics = "${general.kafka-topic}")
    public void listen(Transaction tx) {

        Optional<UserRecord> senderOpt = userRepository.findById(tx.getSenderId());
        Optional<UserRecord> recipientOpt = userRepository.findById(tx.getRecipientId());

        if (senderOpt.isEmpty() || recipientOpt.isEmpty()) return;

        UserRecord sender = senderOpt.get();
        UserRecord recipient = recipientOpt.get();

        if (sender.getBalance() < tx.getAmount()) return;

        // 1️⃣ Call incentive API
        float incentive = incentiveClient.fetchIncentive(tx);

        // 2️⃣ Persist transaction
        TransactionRecord record = new TransactionRecord();
        record.setSender(sender);
        record.setRecipient(recipient);
        record.setAmount(tx.getAmount());
        record.setIncentive(incentive);

        transactionRepository.save(record);

        // 3️⃣ Update balances
        sender.setBalance(sender.getBalance() - tx.getAmount());
        recipient.setBalance(recipient.getBalance() + tx.getAmount() + incentive);

        userRepository.save(sender);
        userRepository.save(recipient);

    }
}
