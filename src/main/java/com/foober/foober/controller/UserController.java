package com.foober.foober.controller;

import com.foober.foober.config.CurrentUser;
import com.foober.foober.dto.*;
import com.foober.foober.model.User;
import com.foober.foober.service.UserService;
import com.foober.foober.util.GeneralUtils;
import lombok.AllArgsConstructor;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static com.foober.foober.util.GeneralUtils.TEMPLATE_IMAGE;

@RestController
@AllArgsConstructor
@RequestMapping("/user")
public class UserController {
    private UserService userService;

    @GetMapping("/get/{username}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ApiResponse<UserBriefDisplay> getUserByUsername(@PathVariable String username) {
        String image = TEMPLATE_IMAGE;
        User user = this.userService.findByUsername(username);
        if (user.getImage() != null)
            image = Base64.encodeBase64String(user.getImage().getData());
        return new ApiResponse<>(new UserBriefDisplay(user.getDisplayName(), user.getUsername(), image));
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ApiResponse<UserInfo> getCurrentUser(@CurrentUser LocalUser user) {
        return new ApiResponse<>(GeneralUtils.buildUserInfo(user));
    }
    @PutMapping("/update")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ApiResponse<?> updateUser(@Valid @RequestBody UpdateRequest updateRequest, @CurrentUser LocalUser user) {
        userService.update(updateRequest, user.getUser());
        return new ApiResponse<>(HttpStatus.OK, "Successfully updated user.");
    }
    @PutMapping("/update-password")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ApiResponse<?> updateUserPassword(@Valid @RequestBody PasswordUpdateRequest updateRequest, @CurrentUser LocalUser user) {
        userService.updatePassword(updateRequest, user.getUser());
        return new ApiResponse<>(HttpStatus.OK, "Successfully updated password.");
    }
    @PostMapping("/forgot-password")
    public ApiResponse<?> forgotPassword(@Valid @RequestBody ForgotPasswordRequest resetRequest) {
        userService.sendPasswordResetEmail(resetRequest.getEmail());
        return new ApiResponse<>(HttpStatus.OK, "Successfully sent the request. Link you receive in the email will expire in 15 minutes.");
    }
    @PutMapping("/reset-password")
    public ApiResponse<?> resetUserPassword(@Valid @RequestBody PasswordResetRequest resetRequest) {
        userService.resetPassword(resetRequest);
        return new ApiResponse<>(HttpStatus.OK, "Successfully updated password.");
    }

    @PutMapping("/{id}/enable")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ApiResponse<?> enableUser(@PathVariable Long id) {
        this.userService.updateIsEnabled(id, true);
        return new ApiResponse<>(HttpStatus.OK);
    }

    @PutMapping("/{id}/disable")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ApiResponse<?> disableUser(@PathVariable Long id) {
        this.userService.updateIsEnabled(id, true);
        return new ApiResponse<>(HttpStatus.OK);
    }

}
