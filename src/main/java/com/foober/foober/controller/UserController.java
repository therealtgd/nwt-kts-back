package com.foober.foober.controller;

import com.foober.foober.config.CurrentUser;
import com.foober.foober.dto.ApiResponse;
import com.foober.foober.dto.LocalUser;
import com.foober.foober.dto.UpdateRequest;
import com.foober.foober.dto.UserInfo;
import com.foober.foober.service.UserService;
import com.foober.foober.util.GeneralUtils;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@AllArgsConstructor
@RequestMapping("/user")
public class UserController {
    private UserService userService;

    @GetMapping("/me")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<UserInfo> getCurrentUser(@CurrentUser LocalUser user) {
        return ResponseEntity.ok(GeneralUtils.buildUserInfo(user));
    }

    @PutMapping("/update")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ApiResponse updateUser(@Valid @RequestBody UpdateRequest updateRequest, @CurrentUser LocalUser user) {
        userService.update(updateRequest, user.getUser());
        return new ApiResponse(true, "Successfully updated user.");
    }
}
