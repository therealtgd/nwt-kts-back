package com.foober.foober.service;

import com.foober.foober.dto.SessionDisplayDTO;
import com.foober.foober.exception.UserNotFoundException;
import com.foober.foober.model.User;
import com.foober.foober.repos.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public User findByUsername(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isEmpty())
            throw new UserNotFoundException();
        return user.get();
    }

    public SessionDisplayDTO whoAmI(Authentication auth) {
        User user = (User) auth.getPrincipal();
        return new SessionDisplayDTO(user.getImage(), user.getFirstName(), user.getLastName(), user.getAuthority().getName());
    }

}
