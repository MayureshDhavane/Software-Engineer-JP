package com.jpmc.midascore.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TransactionProcessorTest {

    @Test
    public void testCalculateDailyCreditTotal() {
        TransactionProcessor processor = new TransactionProcessor();
        LocalDate today = LocalDate.of(2024, 1, 15);

        List<TransactionProcessor.Transaction> transactions = Arrays.asList(
                new TransactionProcessor.Transaction("ACC001", new BigDecimal("100.00"), today, "CREDIT"),
                new TransactionProcessor.Transaction("ACC001", new BigDecimal("250.50"), today, "CREDIT"),
                new TransactionProcessor.Transaction("ACC001", new BigDecimal("50.00"), today, "DEBIT"),
                new TransactionProcessor.Transaction("ACC002", new BigDecimal("300.00"), today, "CREDIT"),
                new TransactionProcessor.Transaction("ACC001", new BigDecimal("75.25"), today, "CREDIT")
        );

        BigDecimal result = processor.calculateDailyCreditTotal(transactions, "ACC001", today);

        // Expected: 100.00 + 250.50 + 75.25 = 425.75
        assertEquals(new BigDecimal("425.75"), result);
    }
}