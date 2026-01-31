package com.jpmc.midascore.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class InterestCalculator {

    /**
     * Calculates interest earned over a period of months.
     * Formula: Interest = Balance * (AnnualRate / 12 months) * NumberOfMonths
     */
    public BigDecimal calculateInterest(BigDecimal balance, int annualRatePercent, int months) {
        if (balance == null || balance.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }

        BigDecimal monthlyRate = new BigDecimal(annualRatePercent / 12 / 100.0);

        BigDecimal interest = balance.multiply(monthlyRate)
                .multiply(new BigDecimal(months));

        return interest.setScale(2, RoundingMode.HALF_UP);
    }
}