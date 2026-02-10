package com.jpmc.midascore.kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Incentive;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.repository.TransactionRepository;
import com.jpmc.midascore.repository.UserRepository;
import com.jpmc.midascore.service.IncentiveProvider;

@Component
public class TransactionListener {
	@Autowired
	private IncentiveProvider incentiveProvider;
	@Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionRepository transactionRepository;

	@KafkaListener(topics = "${general.kafka-topic}")
    public void listen(Transaction transaction) {
        
		
        //System.out.println("Received transaction: " + transaction.getAmount());
		// 1. Fetch users from the DB
        UserRecord sender = userRepository.findById(transaction.getSenderId());
        UserRecord recipient = userRepository.findById(transaction.getRecipientId());

        // 2. Validate users exist and sender has sufficient balance
        if (sender != null && recipient != null && sender.getBalance() >= transaction.getAmount()) {
            
        	Incentive incentive = incentiveProvider.getIncentive(transaction);
        	float incentiveAmount =0f;
        	if (incentive != null) {
        	    incentiveAmount = incentive.getAmount();
        	}
        	
            // 3. Perform the math
            sender.setBalance(sender.getBalance() - transaction.getAmount());
            recipient.setBalance(recipient.getBalance() + transaction.getAmount() + incentiveAmount);

            // 4. Save the updated users back to the database
            userRepository.save(sender);
            userRepository.save(recipient);

            // 5. Create the record of this specific transaction
            TransactionRecord record = new TransactionRecord(sender, recipient, transaction.getAmount());
            transactionRepository.save(record);
            
        }
    }
}