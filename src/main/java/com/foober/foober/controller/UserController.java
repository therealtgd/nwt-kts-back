package com.foober.foober.controller;

import com.foober.foober.dto.LocalUser;
import com.foober.foober.exception.ResourceNotFoundException;
import com.foober.foober.model.User;
import com.foober.foober.repos.UserRepository;
import com.foober.foober.config.CurrentUser;
import com.foober.foober.util.GeneralUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @GetMapping("/user/me")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<?> getCurrentUser(@CurrentUser LocalUser user) {
        return ResponseEntity.ok(GeneralUtils.buildUserInfo(user));
    }

}
