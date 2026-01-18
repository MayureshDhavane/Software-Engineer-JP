package com.jpmc.midascore.component;

import com.jpmc.midascore.foundation.Incentive;
import com.jpmc.midascore.foundation.Transaction;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class IncentiveConduit {

    private final RestTemplate restTemplate = new RestTemplate();
    private static final String INCENTIVE_URL = "http://localhost:8080/incentive";

    public Incentive fetchIncentive(Transaction transaction) {
        Incentive incentive = restTemplate.postForObject(
                INCENTIVE_URL,
                transaction,              // ✅ let Spring serialize Transaction into JSON
                Incentive.class           // ✅ deserialize JSON response into Incentive
        );

        return incentive != null ? incentive : new Incentive(0f);
    }
}
