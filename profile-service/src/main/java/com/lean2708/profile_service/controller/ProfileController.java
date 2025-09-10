package com.lean2708.profile_service.controller;

import com.lean2708.profile_service.dto.request.UpdateProfileRequest;
import com.lean2708.profile_service.dto.response.ApiResponse;
import com.lean2708.profile_service.dto.response.UserProfileResponse;
import com.lean2708.profile_service.service.UserProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j(topic = "PROFILE-CONTROLLER")
@RestController
@Validated
@RequestMapping("/profiles")
@RequiredArgsConstructor
public class ProfileController {

    private final UserProfileService userProfileService;

    @GetMapping("/{userId}")
    public ApiResponse<UserProfileResponse> createProfile(@PathVariable Long userId) {
        log.info("Receive get profile for userId={}", userId);

        return ApiResponse.<UserProfileResponse>builder()
                .code(HttpStatus.CREATED.value())
                .message("Get Profile For UserId")
                .result(userProfileService.getProfileByUserId(userId))
                .build();
    }

    @PutMapping("/{userId}")
    public ApiResponse<UserProfileResponse> updateProfile(@PathVariable Long userId,
                                                          @Valid @RequestBody UpdateProfileRequest request) {
        log.info("Receive update profile for userId={}", userId);

        return ApiResponse.<UserProfileResponse>builder()
                .code(HttpStatus.OK.value())
                .message("Profile updated successfully")
                .result(userProfileService.updateProfile(userId, request))
                .build();
    }

}
