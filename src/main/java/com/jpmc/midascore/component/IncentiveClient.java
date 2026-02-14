package com.jpmc.midascore.component;

import com.jpmc.midascore.foundation.Incentive;
import com.jpmc.midascore.foundation.Transaction;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class IncentiveClient {
    private final RestTemplate restTemplate;

    public IncentiveClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public float fetchIncentive(Transaction tx) {
        Incentive incentive = restTemplate.postForObject(
                "http://localhost:8080/incentive",
                tx,
                Incentive.class
        );

        return incentive != null ? incentive.getAmount() : 0f;
    }
}
