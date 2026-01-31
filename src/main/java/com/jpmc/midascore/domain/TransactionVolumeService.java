package com.jpmc.midascore.domain;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
     *
     * THE BUG: If the same transactionId appears twice (due to a retry or
     * network glitch), it gets added twice to the total.
     */
    public BigDecimal calculateTotalVolume(List<TransactionRecord> transactions) {
        BigDecimal totalVolume = BigDecimal.ZERO;
        Set<String> processedIds = new HashSet<>();
//        for (TransactionRecord record : transactions) {
//            // As of now we are just adding everything in the list we have to remove the duplicates
//            String currentTransactionId = record.getTransactionId();
//            if (processedIds.contains(currentTransactionId)) {
//                continue;
//            }
//            totalVolume = totalVolume.add(record.getAmount());
//            processedIds.add(currentTransactionId);
//        }

//        for(TransactionRecord record : transactions) {
//            String transactionId = record.getTransactionId();
//            if(!processedIds.add(transactionId)) {
//                continue;
//            }
//            totalVolume = totalVolume.add(record.getAmount());
//        }

        // Using Streams
        return transactions.stream()
                .collect(Collectors.toMap(
                        TransactionRecord::getTransactionId,
                        TransactionRecord::getAmount,
                        (existing, replacement) -> existing ))  // which keeps the first amount for duplicates
                .values()
                .stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);
//        return totalVolume;
    }
}