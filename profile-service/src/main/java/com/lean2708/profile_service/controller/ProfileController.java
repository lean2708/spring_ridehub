package com.lean2708.profile_service.controller;

import com.lean2708.profile_service.dto.request.UserProfileRequest;
import com.lean2708.profile_service.dto.response.ApiResponse;
import com.lean2708.profile_service.dto.response.UserProfileResponse;
import com.lean2708.profile_service.entity.UserProfile;
import com.lean2708.profile_service.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Slf4j(topic = "PROFILE-CONTROLLER")
@RestController
@RequestMapping("/profiles")
@RequiredArgsConstructor
public class ProfileController {

    private final UserProfileService userProfileService;

    @GetMapping("/{userId}")
    public ApiResponse<UserProfileResponse> createProfile(@PathVariable Long userId) {
        log.info("Get profile for userId={}", userId);

        return ApiResponse.<UserProfileResponse>builder()
                .code(HttpStatus.CREATED.value())
                .message("Get Profile For UserId")
                .result(userProfileService.getProfileByUserId(userId))
                .build();
    }
}
