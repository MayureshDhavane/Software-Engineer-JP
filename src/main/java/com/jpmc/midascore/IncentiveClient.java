package com.jpmc.midascore;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;

import com.jpmc.midascore.foundation.Incentive;

public class IncentiveClient {
        private final RestTemplate restTemplate;

    @Value("${incentive.api.url}")
    private String incentiveUrl;

    public IncentiveClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Incentive fetchIncentive(Transaction transaction) {
        return restTemplate.postForObject(
                incentiveUrl,
                transaction,
                Incentive.class
        );
    }
}
