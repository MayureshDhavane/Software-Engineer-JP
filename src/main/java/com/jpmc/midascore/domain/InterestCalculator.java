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

//         integer division causes 6/12 = 0 and 3/12 = 0
        BigDecimal annualRateDecimal = new BigDecimal(annualRatePercent).divide(new BigDecimal(100));
        // Divided by 12 to get the monthly rate (0.06 / 12 = 0.005)
        BigDecimal monthlyRate = annualRateDecimal.divide(new BigDecimal(12), 10, RoundingMode.HALF_UP);

        BigDecimal interest = balance.multiply(monthlyRate)
                .multiply(new BigDecimal(months));

        return interest.setScale(2, RoundingMode.HALF_UP);
    }
}