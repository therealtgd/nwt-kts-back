package com.foober.foober.controller;

import com.foober.foober.config.JwtUtil;
import com.foober.foober.dao.UserDao;
import com.foober.foober.dto.AuthenticationRequest;
import com.foober.foober.exception.UserNotFoundException;
import com.foober.foober.model.User;
import com.foober.foober.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/v1/auth/", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final UserDao userDao;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder;

    @PostMapping
    public ResponseEntity<String> authenticate(@RequestBody AuthenticationRequest request) {
        UsernamePasswordAuthenticationToken authenticationToken =
            new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword());
        Authentication authentication = authenticationManager.authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        final UserDetails user = userDao.findUserByUsername(request.getUsername());
        if (user != null) {
            return ResponseEntity.ok(jwtUtil.generateToken(user));
        }

        return ResponseEntity.status(400).body("Some error has occurred.");
    }
    //TODO: Check if user is enabled/active
    @PostMapping("login")
    public ResponseEntity<String> login(@RequestBody AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        passwordEncoder.encode(request.getPassword())
                )
        );

        try {
            User user = userService.findUserByUsername(request.getUsername());
            return ResponseEntity.ok(jwtUtil.generateToken(user));
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(400).body("Some error has occurred.");
        }
    }
}
