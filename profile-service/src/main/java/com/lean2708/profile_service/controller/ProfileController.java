package com.lean2708.profile_service.controller;

import com.lean2708.common_library.dto.response.ApiResponse;
import com.lean2708.profile_service.dto.request.UpdateProfileRequest;
import com.lean2708.profile_service.dto.response.UserProfileResponse;
import com.lean2708.profile_service.service.UserProfileService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j(topic = "PROFILE-CONTROLLER")
@RestController
@Validated
@RequiredArgsConstructor
public class ProfileController {

    private final UserProfileService userProfileService;

    @GetMapping("/users/{userId}")
    public ApiResponse<UserProfileResponse> createProfile(@PathVariable Long userId) {
        log.info("Receive get profile for userId={}", userId);

        return ApiResponse.<UserProfileResponse>builder()
                .code(HttpStatus.CREATED.value())
                .message("Get Profile For UserId")
                .result(userProfileService.getProfileByUserId(userId))
                .build();
    }

    @PutMapping("/users/{userId}")
    public ApiResponse<UserProfileResponse> updateProfile(@PathVariable Long userId,
                                                          @Valid @RequestBody UpdateProfileRequest request) {
        log.info("Receive update profile for userId={}", userId);

        return ApiResponse.<UserProfileResponse>builder()
                .code(HttpStatus.OK.value())
                .message("Profile updated successfully")
                .result(userProfileService.updateProfile(userId, request))
                .build();
    }

    @Operation(summary = "Upload Avatar",
            description = "API để update avatar")
    @PutMapping(value = "/users/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ApiResponse<UserProfileResponse> updateAvatar(@RequestPart("file") MultipartFile file) {
        log.info("Received request to Update Avatar");

        return ApiResponse.<UserProfileResponse>builder()
                .code(HttpStatus.OK.value())
                .message("Update Avatar")
                .result(userProfileService.updateAvatar(file))
                .build();
    }

}
