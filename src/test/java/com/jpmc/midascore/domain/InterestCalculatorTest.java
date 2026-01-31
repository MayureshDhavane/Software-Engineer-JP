package com.jpmc.midascore.domain;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class InterestCalculatorTest {

    @Test
    public void testInterestForSixMonths() {
        InterestCalculator calculator = new InterestCalculator();

        // Scenario: $10,000 balance, 6% annual rate, for 6 months.
        // Math: 10,000 * (0.06 / 12) * 6 = $300.00
        BigDecimal result = calculator.calculateInterest(new BigDecimal("10000.00"), 6, 6);

        assertEquals(new BigDecimal("300.00"), result);
    }

    @Test
    public void testSmallInterest() {
        InterestCalculator calculator = new InterestCalculator();

        // Scenario: $1,000 balance, 3% annual rate, for 1 month.
        // Math: 1,000 * (0.03 / 12) * 1 = $2.50
        BigDecimal result = calculator.calculateInterest(new BigDecimal("1000.00"), 3, 1);

        assertEquals(new BigDecimal("2.50"), result);
    }
}