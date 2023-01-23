package com.foober.foober.controller;

import com.foober.foober.config.JwtUtil;
import com.foober.foober.config.TokenResponse;
import com.foober.foober.dto.AuthenticationRequest;
import com.foober.foober.dto.SessionDisplayDTO;
import com.foober.foober.model.User;
import com.foober.foober.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping(value = "/auth/", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtUtil jwtUtil;
/*
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
    */

    //TODO: Check if user is enabled/active
    @PostMapping("login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody AuthenticationRequest request) {

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword());
        Authentication authentication = authenticationManager.authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        User user = (User) authentication.getPrincipal();

        return ResponseEntity.ok(new TokenResponse(jwtUtil.generateToken(user)));

    }

    @GetMapping(value = "whoami")
    public SessionDisplayDTO whoAmI(Authentication auth) {
        return userService.whoAmI(auth);
    }
}
