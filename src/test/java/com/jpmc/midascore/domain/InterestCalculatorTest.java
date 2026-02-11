package com.jpmc.midascore.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InterestCalculatorTest {

    @Test
    public void testInterestForSixMonths() {
        InterestCalculator calculator = new InterestCalculator();

        // Scenario: $10,000 balance, 6% annual rate, for 6 months.
        // Monthly rate = 6% / 12 = 0.5% (or 0.005)
        // Expected Interest = 10,000 * 0.005 * 6 = $300.00

        BigDecimal result = calculator.calculateInterest(new BigDecimal("10000.00"), 6, 6);

        assertEquals(new BigDecimal("300.00"), result);
    }

    @Test
    public void testSmallInterest() {
        InterestCalculator calculator = new InterestCalculator();

        // Scenario: $1,000 balance, 3% annual rate, for 1 month.
        // Monthly rate = 3% / 12 = 0.25% (0.0025)
        // Expected Interest = 1,000 * 0.0025 * 1 = $2.50

        BigDecimal result = calculator.calculateInterest(new BigDecimal("1000.00"), 3, 1);

        assertEquals(new BigDecimal("2.50"), result);
    }
}