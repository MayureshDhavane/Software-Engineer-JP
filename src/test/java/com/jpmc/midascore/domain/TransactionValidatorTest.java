package com.jpmc.midascore.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TransactionValidatorTest {

    @Test
    public void testFindInsufficientFundAccounts() {
        TransactionValidator validator = new TransactionValidator();

        // Setup accounts
        List<TransactionValidator.Account> accounts = Arrays.asList(
                new TransactionValidator.Account("ACC001", new BigDecimal("1000.00")),
                new TransactionValidator.Account("ACC002", new BigDecimal("500.00")),
                new TransactionValidator.Account("ACC003", new BigDecimal("200.00"))
        );

        // Setup transfer requests
        List<TransactionValidator.TransferRequest> transfers = Arrays.asList(
                new TransactionValidator.TransferRequest("ACC001", "ACC002", new BigDecimal("300.00")),
                new TransactionValidator.TransferRequest("ACC002", "ACC003", new BigDecimal("100.00")),
                new TransactionValidator.TransferRequest("ACC003", "ACC001", new BigDecimal("50.00"))
        );

        // Find accounts that would have insufficient funds
        List<String> problematic = validator.findInsufficientFundAccounts(accounts, transfers);

        // Expected: No accounts should have insufficient funds
        assertTrue(problematic.isEmpty(),
                "Expected no accounts with insufficient funds, but found: " + problematic);
    }

    @Test
    public void testOriginalAccountsUnmodified() {
        TransactionValidator validator = new TransactionValidator();

        // Setup accounts
        TransactionValidator.Account acc1 = new TransactionValidator.Account("ACC001", new BigDecimal("1000.00"));
        TransactionValidator.Account acc2 = new TransactionValidator.Account("ACC002", new BigDecimal("500.00"));

        List<TransactionValidator.Account> accounts = Arrays.asList(acc1, acc2);

        // Setup a transfer
        List<TransactionValidator.TransferRequest> transfers = Arrays.asList(
                new TransactionValidator.TransferRequest("ACC001", "ACC002", new BigDecimal("300.00"))
        );

        // Run validation
        validator.findInsufficientFundAccounts(accounts, transfers);

        // CRITICAL: Original account objects should NOT be modified
        assertEquals(new BigDecimal("1000.00"), acc1.getBalance(),
                "ACC001 balance should remain unchanged");
        assertEquals(new BigDecimal("500.00"), acc2.getBalance(),
                "ACC002 balance should remain unchanged");
    }
}