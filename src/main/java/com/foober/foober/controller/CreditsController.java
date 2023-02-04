package com.foober.foober.controller;

import com.foober.foober.config.CurrentUser;
import com.foober.foober.dto.ApiResponse;
import com.foober.foober.dto.CreditsDto;
import com.foober.foober.dto.LocalUser;
import com.foober.foober.service.CreditsService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/credits" ,produces = MediaType.APPLICATION_JSON_VALUE)
@AllArgsConstructor
public class CreditsController {

    private final CreditsService creditsService;

    @GetMapping("/amounts")
    public ApiResponse<int[]> getAmounts() {
        return new ApiResponse<>(creditsService.getCreditAmounts());
    }

    @PostMapping("/add")
    public ApiResponse<String> addCredits(@RequestBody CreditsDto creditsDto, @CurrentUser LocalUser user) {
        try {
            creditsService.addCredits(user.getUser(), creditsDto.getAmount());
            return new ApiResponse<>("Added credits");
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR, "Failed adding credits");
        }
    }
}
