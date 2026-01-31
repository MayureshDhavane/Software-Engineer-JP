package com.jpmc.midascore.domain;

import java.math.BigDecimal;
import java.util.List;

public class TransactionVolumeService {

    public static class TransactionRecord {
        private String transactionId; // Unique ID for each transaction
        private BigDecimal amount;

        public TransactionRecord(String transactionId, BigDecimal amount) {
            this.transactionId = transactionId;
            this.amount = amount;
        }

        public String getTransactionId() { return transactionId; }
        public BigDecimal getAmount() { return amount; }
    }

    /**
     * Calculates the total volume of all transactions.
     */
    public BigDecimal calculateTotalVolume(List<TransactionRecord> transactions) {
        BigDecimal totalVolume = BigDecimal.ZERO;

        for (TransactionRecord record : transactions) {
            totalVolume = totalVolume.add(record.getAmount());
        }

        return totalVolume;
    }
}