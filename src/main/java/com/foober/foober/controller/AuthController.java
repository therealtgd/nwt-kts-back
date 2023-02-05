package com.foober.foober.controller;

import com.foober.foober.config.CurrentUser;
import com.foober.foober.dto.ClientSignUpRequest;
import com.foober.foober.dto.LocalUser;
import com.foober.foober.dto.LoginRequest;
import com.foober.foober.exception.UserIsNotActivatedException;
import com.foober.foober.model.User;
import com.foober.foober.security.jwt.TokenProvider;
import com.foober.foober.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    public ResponseEntity<String> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        if (!userService.isActivated(loginRequest.getEmail())) {
            throw new UserIsNotActivatedException("Bad credentials.");
        }
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.createToken(authentication);
        userService.setOnlineUser(loginRequest.getEmail());
        return ResponseEntity.ok(jwt);
    }

    @PostMapping("/get-local-user")
    public ResponseEntity<LocalUser> getLocalUser(@Valid @RequestBody LoginRequest loginRequest) {
        if (!userService.isActivated(loginRequest.getEmail())) {
            throw new UserIsNotActivatedException("Bad credentials.");
        }
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return ResponseEntity.ok((LocalUser) authentication.getPrincipal());
    }

    @PostMapping("/signup")
    public ResponseEntity<String> registerUser(@Valid @RequestPart("signupRequest") ClientSignUpRequest signUpRequest, @RequestPart(value = "image", required=false) MultipartFile image) {
        User user = userService.registerNewUser(signUpRequest, image);
        return ResponseEntity.ok(user.getEmail());
    }

    @PutMapping("/signout")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<String> signOut(@CurrentUser LocalUser user) {
        this.userService.setOfflineUser(user.getUser());
        return ResponseEntity.ok(user.getUsername()+" signed out successfully.");
    }

}
