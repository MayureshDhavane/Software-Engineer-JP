package com.jpmc.midascore.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class TransactionValidator {

    public static class Account {
        private String id;
        private BigDecimal balance;

        public Account(String id, BigDecimal balance) {
            this.id = id;
            this.balance = balance;
        }

        public String getId() { return id; }
        public BigDecimal getBalance() { return balance; }
        public void setBalance(BigDecimal balance) { this.balance = balance; }

        @Override
        public String toString() {
            return "Account{id='" + id + "', balance=" + balance + "}";
        }
    }

    public static class TransferRequest {
        private String fromAccountId;
        private String toAccountId;
        private BigDecimal amount;

        public TransferRequest(String fromAccountId, String toAccountId, BigDecimal amount) {
            this.fromAccountId = fromAccountId;
            this.toAccountId = toAccountId;
            this.amount = amount;
        }

        public String getFromAccountId() { return fromAccountId; }
        public String getToAccountId() { return toAccountId; }
        public BigDecimal getAmount() { return amount; }
    }

    /**
     * Validates a series of transfer requests and returns which accounts
     * would have insufficient funds after all transfers are processed.
     *
     * Returns a list of account IDs that would go negative.
     */
    public List<String> findInsufficientFundAccounts(List<Account> accounts,
                                                     List<TransferRequest> transfers) {
        List<String> problematicAccounts = new ArrayList<>();
        // Created a working copy of accounts to avoid modifying originals
        List<Account> accountCopies = new ArrayList<>();
        for (Account account : accounts) {
            accountCopies.add(new Account(account.getId(), account.getBalance()));
        }

        for (TransferRequest transfer : transfers) {
            Account fromAccount = findAccount(accountCopies, transfer.getFromAccountId());
            Account toAccount = findAccount(accountCopies, transfer.getToAccountId());

            if (fromAccount == null || toAccount == null) {
                continue; // Skip invalid transfers
            }

            // Simulate the transfer
            BigDecimal newBalance = fromAccount.getBalance().subtract(transfer.getAmount());
            fromAccount.setBalance(newBalance);

            BigDecimal newToBalance = toAccount.getBalance().add(transfer.getAmount());
            toAccount.setBalance(newToBalance);

            // Check if sender would go negative
            if (fromAccount.getBalance().compareTo(BigDecimal.ZERO) < 0) {
                if (!problematicAccounts.contains(fromAccount.getId())) {
                    problematicAccounts.add(fromAccount.getId());
                }
            }
        }

        return problematicAccounts;
    }

    private Account findAccount(List<Account> accounts, String accountId) {
        for (Account account : accounts) {
            if (account.getId().equals(accountId)) {
                return account;
            }
        }
        return null;
    }
}