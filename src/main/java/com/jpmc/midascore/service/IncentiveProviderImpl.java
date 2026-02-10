package com.jpmc.midascore.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.jpmc.midascore.foundation.Incentive;
import com.jpmc.midascore.foundation.Transaction;

@Service
public class IncentiveProviderImpl implements IncentiveProvider {
private final RestTemplate restTemplate;

public IncentiveProviderImpl(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
}
	@Override
	public Incentive getIncentive(Transaction transaction) {
		
		return restTemplate.postForObject("http://localhost:8080/incentive", transaction, Incentive.class);
	}

}
