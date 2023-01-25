package com.foober.foober.service;

import com.foober.foober.dto.LocalUser;
import com.foober.foober.exception.ResourceNotFoundException;
import com.foober.foober.model.User;
import com.foober.foober.util.GeneralUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("localUserDetailService")
public class LocalUserDetailService implements UserDetailsService {

    @Autowired
    private UserService userService;

    @Override
    @Transactional
    public LocalUser loadUserByUsername(final String email) throws UsernameNotFoundException {
        User user = userService.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("User " + email + " was not found in the database");
        }
        return createLocalUser(user);
    }

    @Transactional
    public LocalUser loadUserById(Long id) {
        User user = userService.findById(id).orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        return createLocalUser(user);
    }

    /**
     * @param user
     * @return
     */
    private LocalUser createLocalUser(User user) {
        return new LocalUser(user.getEmail(), user.getPassword(), user.isEnabled(), true, true, true, GeneralUtils.buildSimpleGrantedAuthorities(user.getAuthorities()), user);
    }
}