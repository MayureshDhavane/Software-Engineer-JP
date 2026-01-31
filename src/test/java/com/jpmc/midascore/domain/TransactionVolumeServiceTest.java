package com.jpmc.midascore.domain;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TransactionVolumeServiceTest {

    @Test
    public void testCalculateTotalVolume_IgnoreDuplicates() {
        TransactionVolumeService service = new TransactionVolumeService();

        // Scenario: 3 transactions, but "TXN_101" is a duplicate sent twice.
        List<TransactionVolumeService.TransactionRecord> transactions = Arrays.asList(
                new TransactionVolumeService.TransactionRecord("TXN_101", new BigDecimal("500.00")),
                new TransactionVolumeService.TransactionRecord("TXN_102", new BigDecimal("300.00")),
                new TransactionVolumeService.TransactionRecord("TXN_101", new BigDecimal("500.00")) // DUPLICATE!
        );

        BigDecimal total = service.calculateTotalVolume(transactions);

        // Expected: 500 + 300 = 800.00
        // Actual will be: 1300.00 (because 500 is added twice)
        assertEquals(new BigDecimal("800.00"), total, "The service should ignore duplicate transaction IDs.");
    }
}