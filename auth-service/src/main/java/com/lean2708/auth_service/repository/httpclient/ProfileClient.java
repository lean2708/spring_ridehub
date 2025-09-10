package com.lean2708.auth_service.repository.httpclient;

import com.lean2708.auth_service.dto.request.UserProfileRequest;
import com.lean2708.auth_service.dto.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "profile-service")
public interface ProfileClient {

    @PostMapping("/internal/profiles")
    ApiResponse<Void> createProfile(UserProfileRequest request);

}
