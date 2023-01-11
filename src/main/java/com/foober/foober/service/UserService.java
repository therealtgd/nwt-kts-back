package com.foober.foober.service;

import com.foober.foober.exception.UserNotFoundException;
import com.foober.foober.model.User;
import com.foober.foober.repos.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public User findUserByUsername(String username) throws UserNotFoundException {
        Optional<User> user = userRepository.findUserByUsername(username);
        if (user.isEmpty())
            throw new UserNotFoundException();
        return user.get();
    }
}
