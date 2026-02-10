package com.jpmc.midascore.service;

import com.jpmc.midascore.foundation.Balance;

public interface BalanceProvider {
Balance getBalance(Long userId);
}
