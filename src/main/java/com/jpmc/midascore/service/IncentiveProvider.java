package com.jpmc.midascore.service;

import com.jpmc.midascore.foundation.Incentive;
import com.jpmc.midascore.foundation.Transaction;

public interface IncentiveProvider {
Incentive getIncentive(Transaction transaction);
}
