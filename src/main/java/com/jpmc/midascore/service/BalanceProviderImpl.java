package com.jpmc.midascore.service;

import org.springframework.stereotype.Service;

import com.jpmc.midascore.foundation.Balance;
import com.jpmc.midascore.repository.UserRepository;

@Service
public class BalanceProviderImpl implements BalanceProvider {
	
	private final UserRepository userRepository;
	
	public BalanceProviderImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
	
	@Override
	public Balance getBalance(Long userId) {
	
		return userRepository.findById(userId)
				.map(user-> new Balance(user.getBalance()))
				.orElseGet(()-> new Balance(0));
	}

}
