package com.jpmc.midascore;

import com.jpmc.midascore.foundation.Incentive;
import com.jpmc.midascore.foundation.Transaction;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class IncentiveClient {

    private final RestTemplate restTemplate;

    public IncentiveClient(RestTemplateBuilder builder) {
        this.restTemplate = builder.build();
    }

    public Incentive fetchIncentive(Transaction transaction) {
        try {
            String url = "http://localhost:8080/incentive";
            return restTemplate.postForObject(url, transaction, Incentive.class);
        } catch (Exception e) {
            Incentive incentive = new Incentive();
            incentive.setAmount(0);
            return incentive;
        }
    }
}