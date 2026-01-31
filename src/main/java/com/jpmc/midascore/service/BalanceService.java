package com.jpmc.midascore.service;

import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Balance;
import com.jpmc.midascore.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class BalanceService {

    private final UserRepository userRepository;

    @Autowired
    public BalanceService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Balance getBalanceForUser(Long userId) {
        Optional<UserRecord> userOptional = userRepository.findById(userId);

        // Check if user exists using Optional
        if (userOptional.isPresent()) {
            UserRecord user = userOptional.get();
            return new Balance(user.getBalance());
        } else {
            // Return balance of 0 if user doesn't exist
            return new Balance(0.0f);
        }
    }
}