package com.jpmc.midascore.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class TransactionProcessor {

    public static class Transaction {
        private String accountId;
        private BigDecimal amount;
        private LocalDate date;
        private String type; // "DEBIT" or "CREDIT"

        public Transaction(String accountId, BigDecimal amount, LocalDate date, String type) {
            this.accountId = accountId;
            this.amount = amount;
            this.date = date;
            this.type = type;
        }

        public String getAccountId() { return accountId; }
        public BigDecimal getAmount() { return amount; }
        public LocalDate getDate() { return date; }
        public String getType() { return type; }
    }

    /**
     * Calculate total CREDIT amount for a specific account on a given date
     */
    public BigDecimal calculateDailyCreditTotal(List<Transaction> transactions,
                                                String accountId,
                                                LocalDate date) {
        List<Transaction> filtered = transactions.stream()
                .filter(t -> t.getAccountId().equals(accountId))
                .filter(t -> t.getDate().equals(date))
                .filter(t -> t.getType().equals("CREDIT"))
                .collect(Collectors.toList());

        BigDecimal total = BigDecimal.ZERO;
        for (int i = 0; i < filtered.size(); i++) {
            total = total.add(filtered.get(i).getAmount());
        }

        return total;
    }
}