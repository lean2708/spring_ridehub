package com.lean2708.auth_service.repository.httpclient;

import com.lean2708.auth_service.dto.request.UserProfileRequest;
import com.lean2708.common_library.dto.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "profile-service", path = "/profiles")
public interface ProfileClient {

    @PostMapping("/internal/users")
    ApiResponse<Void> createProfile(UserProfileRequest request);

}
