package com.foober.foober.controller;

import javax.validation.Valid;

import com.foober.foober.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.foober.foober.dto.ApiResponse;
import com.foober.foober.dto.AuthResponse;
import com.foober.foober.dto.LocalUser;
import com.foober.foober.dto.LoginRequest;
import com.foober.foober.dto.ClientSignUpRequest;
import com.foober.foober.exception.UserAlreadyExistsException;
import com.foober.foober.security.jwt.TokenProvider;
import com.foober.foober.service.UserService;
import com.foober.foober.util.GeneralUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserService userService;

    @Autowired
    TokenProvider tokenProvider;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.createToken(authentication);
        LocalUser localUser = (LocalUser) authentication.getPrincipal();
        return ResponseEntity.ok(new AuthResponse(jwt, GeneralUtils.buildUserInfo(localUser)));
    }

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse> registerUser(@Valid @RequestBody ClientSignUpRequest signUpRequest) {
        try {
            User user = userService.registerNewUser(signUpRequest);
            return ResponseEntity.ok().body(new ApiResponse(true, user.getEmail()));
        } catch (UserAlreadyExistsException e) {
            log.error("Exception Ocurred", e);
            return new ResponseEntity<>(new ApiResponse(false, "Email Address already in use!"), HttpStatus.BAD_REQUEST);
        }
    }

}
