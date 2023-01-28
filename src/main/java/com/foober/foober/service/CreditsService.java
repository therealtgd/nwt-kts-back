package com.foober.foober.service;

import com.foober.foober.model.User;
import com.foober.foober.repos.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class CreditsService {

    private final int[] CREDIT_AMOUNTS = {100, 200, 500, 1000, 1500, 2000, 3000, 5000, 10000};
    private final UserRepository userRepository;

    public int[] getCreditAmounts() {
        return this.CREDIT_AMOUNTS;
    }

    @Transactional
    public void addCredits(User user, int amount) {
        user.addCredits(amount);
        userRepository.save(user);
    }
}
