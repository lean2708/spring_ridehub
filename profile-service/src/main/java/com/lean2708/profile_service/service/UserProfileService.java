package com.lean2708.profile_service.service;

import com.lean2708.profile_service.dto.request.UpdateProfileRequest;
import com.lean2708.profile_service.dto.request.UserProfileRequest;
import com.lean2708.profile_service.dto.response.UserProfileResponse;
import com.lean2708.profile_service.entity.UserProfile;
import org.springframework.web.multipart.MultipartFile;

public interface UserProfileService {

    void createProfile(UserProfileRequest request);

    UserProfileResponse getProfileByUserId(Long userId);

    UserProfileResponse updateProfile(Long userId, UpdateProfileRequest request);

    UserProfileResponse updateAvatar(MultipartFile file);
}
