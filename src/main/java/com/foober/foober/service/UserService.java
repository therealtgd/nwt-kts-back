package com.foober.foober.service;

import com.foober.foober.dto.*;
import com.foober.foober.model.*;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.foober.foober.exception.*;
import com.foober.foober.repos.RoleRepository;
import com.foober.foober.repos.UserRepository;
import com.foober.foober.security.jwt.TokenProvider;
import com.foober.foober.security.oauth2.user.OAuth2UserInfo;
import com.foober.foober.security.oauth2.user.OAuth2UserInfoFactory;
import com.foober.foober.util.GeneralUtils;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.*;

@Service
@AllArgsConstructor
public class UserService {
    private final TokenProvider tokenUtils;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public User registerNewUser(ClientSignUpRequest signUpRequest) throws UserAlreadyExistsException {

        checkForExistingEmail(signUpRequest.getEmail());
        checkForExistingUsername(signUpRequest.getUsername());

        Client client = buildClient(signUpRequest);
        if (Objects.equals(client.getUsername(), "") || client.getUsername() == null) {
            client.setUsername(client.getEmail());
        }

        try {
            client = userRepository.save(client);
            String token = tokenUtils.generateConfirmationToken(client);
            emailService.sendRegistrationEmail(client, token);
            return client;
        } catch (MessagingException e) {
            throw new EmailNotSentException("Email failed to send.");
        } catch (IOException e) {
            throw new ResourceNotFoundException("Email template was not found.");
        }

    }

    public LocalUser processUserRegistration(String registrationId, Map<String, Object> attributes, OidcIdToken idToken, OidcUserInfo userInfo) {
        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(registrationId, attributes);
        if (StringUtils.isEmpty(oAuth2UserInfo.getName())) {
            throw new OAuth2AuthenticationProcessingException("Name not found from OAuth2 provider");
        } else if (StringUtils.isEmpty(oAuth2UserInfo.getEmail())) {
            throw new OAuth2AuthenticationProcessingException("Email not found from OAuth2 provider");
        }
        ClientSignUpRequest userDetails = toUserRegistrationObject(registrationId, oAuth2UserInfo);
        User user = findByEmail(oAuth2UserInfo.getEmail());
        if (user != null) {
            if (!user.getProvider().equals(registrationId) && !user.getProvider().equals(SocialProvider.LOCAL.getProviderType())) {
                throw new OAuth2AuthenticationProcessingException(
                        "Looks like you're signed up with " + user.getProvider() + " account. Please use your " + user.getProvider() + " account to login.");
            }
            user = updateExistingUser(user, oAuth2UserInfo);
        } else {
            user = registerNewUser(userDetails);
        }

        return LocalUser.create(user, attributes, idToken, userInfo);
    }

    private User updateExistingUser(User existingUser, OAuth2UserInfo oAuth2UserInfo) {
        existingUser.setDisplayName(oAuth2UserInfo.getName());
        return userRepository.save(existingUser);
    }

    private ClientSignUpRequest toUserRegistrationObject(String registrationId, OAuth2UserInfo oAuth2UserInfo) {
        return ClientSignUpRequest.getBuilder().addProviderUserID(oAuth2UserInfo.getId()).addDisplayName(oAuth2UserInfo.getName()).addEmail(oAuth2UserInfo.getEmail())
                .addSocialProvider(GeneralUtils.toSocialProvider(registrationId)).addPassword("changeit").build();
    }

    public void save(User user) {
        userRepository.save(user);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public User findByUsername(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isEmpty())
            throw new UserNotFoundException();
        return user.get();
    }

    public User findByEmail(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        return user.orElse(null);
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }


    private void checkForExistingUsername(String username) {
        if (userRepository.existsByUsername(username))
            throw new UserAlreadyExistsException("Username already exists.");
    }

    private void checkForExistingEmail(String email) {
        if (userRepository.existsByEmail(email))
            throw new EmailAlreadyExistsException("Email already exists.");
    }

    private Client buildClient(final ClientSignUpRequest formDTO) {
        final HashSet<Role> roles = new HashSet<Role>();
        roles.add(roleRepository.findByName("ROLE_CLIENT"));
        roles.add(roleRepository.findByName("ROLE_USER"));
        Client client = new Client(
                formDTO.getUsername(),
                formDTO.getEmail(),
                passwordEncoder.encode(formDTO.getPassword()),
                formDTO.getDisplayName(),
                roles,
                // TODO: Add payment info
                "",
                formDTO.getSocialProvider().getProviderType(),
                formDTO.getProviderUserId()
        );
        return client;
    }

    public void update(UpdateRequest updateRequest, User user) {
        try {
            user.setUsername(updateRequest.getUsername());
            user.setDisplayName(updateRequest.getDisplayName());
            userRepository.save(user);
        }
        catch (DataIntegrityViolationException e) {
            throw new UsernameAlreadyExistsException("Username already exists");
        }
    }

    public void updatePassword(PasswordUpdateRequest updateRequest, User user) {
        try {
            user.setPassword(passwordEncoder.encode(updateRequest.getPassword()));
            userRepository.save(user);
        }
        catch (Exception e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    public void sendPasswordResetEmail(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            throw new UserNotFoundException("User with that email doesn't exist.");
        }
        else if (!user.get().isEnabled()) {
            throw new UserIsNotActivatedException("User with that email doesn't exist.");
        }
        String token = tokenUtils.generateConfirmationToken(user.get());
        try {
            emailService.sendPasswordResetEmail(user.get(), token);
        } catch (MessagingException e) {
            throw new EmailNotSentException("Email failed to send.");
        } catch (IOException e) {
            throw new ResourceNotFoundException("Email template was not found.");
        }
    }

    public void resetPassword(PasswordResetRequest resetRequest) {
        try {
            tokenUtils.validateToken(resetRequest.getToken());
            User user = userRepository.findById(tokenUtils.getUserIdFromToken(resetRequest.getToken())).orElseThrow();
            user.setPassword(passwordEncoder.encode(resetRequest.getPassword()));
            userRepository.save(user);
        } catch (NoSuchElementException e) {
            throw new InvalidTokenException("Token is invalid");
        } catch (TokenExpiredException e) {
            throw new TokenExpiredException("Link had expired");
        } catch (Exception e) {
            throw new BadRequestException(e.getMessage());
        }
    }
    
}
