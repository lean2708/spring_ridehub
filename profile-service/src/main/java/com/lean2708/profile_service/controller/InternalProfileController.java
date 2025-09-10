package com.lean2708.profile_service.controller;

import com.lean2708.profile_service.dto.request.UserProfileRequest;
import com.lean2708.profile_service.dto.response.ApiResponse;
import com.lean2708.profile_service.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j(topic = "INTERNAL-PROFILE-CONTROLLER")
@RestController
@RequestMapping("/internal/profiles")
@RequiredArgsConstructor
public class InternalProfileController {

    private final UserProfileService userProfileService;

    @PostMapping
    public ApiResponse<Void> createProfile(@RequestBody UserProfileRequest request) {
        log.info("Received request to create profile: {}", request);

        userProfileService.createProfile(request);
        return ApiResponse.<Void>builder()
                .code(HttpStatus.CREATED.value())
                .message("Create User Profile")
                .build();
    }
}
