package com.jpmc.midascore.service;

import com.jpmc.midascore.component.DatabaseConduit;
import com.jpmc.midascore.component.IncentiveConduit;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Incentive;
import com.jpmc.midascore.foundation.Transaction;
import org.springframework.stereotype.Service;

@Service
public class TransactionService {

    private final DatabaseConduit databaseConduit;
    private final IncentiveConduit incentiveConduit;

    public TransactionService(DatabaseConduit databaseConduit, IncentiveConduit incentiveConduit) {
        this.databaseConduit = databaseConduit;
        this.incentiveConduit = incentiveConduit;
    }

    public void process(Transaction tx) {


        if (tx == null) return;
        if (tx.getAmount() <= 0) return;

        UserRecord sender = databaseConduit.findById(tx.getSenderId());
        UserRecord recipient = databaseConduit.findById(tx.getRecipientId());

        if (sender == null || recipient == null) return;
        if (sender.getBalance() < tx.getAmount()) return;


        Incentive incentive = incentiveConduit.fetchIncentive(tx);
        float incentiveAmount = (incentive != null) ? incentive.getAmount() : 0f;


        sender.setBalance(sender.getBalance() - tx.getAmount());
        recipient.setBalance(recipient.getBalance() + tx.getAmount() + incentiveAmount);


        databaseConduit.save(sender);
        databaseConduit.save(recipient);


        if ("wilbur".equalsIgnoreCase(recipient.getName())) {
            System.out.println("WILBUR BALANCE => " + recipient.getBalance());
        }
    }
}
