package com.foober.foober.controller;

import com.foober.foober.config.CurrentUser;
import com.foober.foober.dto.*;
import com.foober.foober.model.User;
import com.foober.foober.security.jwt.TokenProvider;
import com.foober.foober.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final TokenProvider tokenProvider;

    @PostMapping("/signin")
    public ApiResponse<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.createToken(authentication);
        userService.setOnlineUser(loginRequest.getEmail());
        return new ApiResponse<>(jwt);
    }

    @PostMapping("/signup")
    public ApiResponse<?> registerUser(@Valid @RequestBody ClientSignUpRequest signUpRequest) {
            User user = userService.registerNewUser(signUpRequest);
            return new ApiResponse<>(user.getEmail());

    }

    @PutMapping("/signout")
    public ApiResponse<?> signOut(@CurrentUser LocalUser user) {
        this.userService.setOfflineUser(user.getUser());
        return new ApiResponse<>(user.getUsername()+" signed out successfully.");
    }

}
