package com.lean2708.profile_service.service.impl;

import com.lean2708.common_library.dto.response.ApiResponse;
import com.lean2708.common_library.exception.ResourceNotFoundException;
import com.lean2708.profile_service.dto.request.UpdateProfileRequest;
import com.lean2708.profile_service.dto.request.UserProfileRequest;
import com.lean2708.profile_service.dto.response.FileResponse;
import com.lean2708.profile_service.dto.response.UserProfileResponse;
import com.lean2708.profile_service.entity.UserProfile;
import com.lean2708.profile_service.mapper.ProfileMapper;
import com.lean2708.profile_service.repository.UserProfileRepository;
import com.lean2708.profile_service.repository.httpclient.FileClient;
import com.lean2708.profile_service.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;

@Slf4j(topic = "USER-PROFILE-SERVICE")
@Service
@RequiredArgsConstructor
public class UserProfileServiceImpl implements UserProfileService {

    private final UserProfileRepository userProfileRepository;
    private final ProfileMapper profileMapper;
    private final FileClient fileClient;

    @Override
    public void createProfile(UserProfileRequest request) {
        log.info("Create profile for userId={}, email={}", request.getUserId(), request.getEmail());

        UserProfile profile = UserProfile.builder()
                .userId(request.getUserId())
                .phone(request.getPhone())
                .name(request.getName())
                .email(request.getEmail())
                .build();

         userProfileRepository.save(profile);
    }

    @Override
    public UserProfileResponse getProfileByUserId(Long userId) {
        log.info("Get Profile By userId={}", userId);

        UserProfile profile = getUserProfileByUserId(userId);

        return profileMapper.toProfileResponse(profile);
    }

    @Override
    public UserProfileResponse updateProfile(Long userId, UpdateProfileRequest request) {
        log.info("Update Profile By userId={}", userId);

        UserProfile profile = getUserProfileByUserId(userId);

        updateProfile(profile, request);

        return profileMapper.toProfileResponse(userProfileRepository.save(profile));
    }

    @Override
    public UserProfileResponse updateAvatar(MultipartFile file) {
        log.info("Update Avatar For User");
        // upload file
        ApiResponse<List<FileResponse>> apiResponse = fileClient.uploadImage(Collections.singletonList(file));
        FileResponse fileResponse = apiResponse.getResult().get(0);

        UserProfile profile = getUserProfileByCurrentUser();
        profile.setAvatarUrl(fileResponse.getImageUrl());
        return profileMapper.toProfileResponse(userProfileRepository.save(profile));
    }


    private UserProfile getUserProfileByUserId(Long userId){
        return userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not exists"));
    }


    private void updateProfile(UserProfile profile, UpdateProfileRequest request) {
        if (request.getName() != null) {
            profile.setName(request.getName());
        }
        if (request.getEmail() != null) {
            profile.setEmail(request.getEmail());
        }
        if (request.getPhone() != null) {
            profile.setPhone(request.getPhone());
        }
        if (request.getDateOfBirth() != null) {
            profile.setDateOfBirth(request.getDateOfBirth());
        }
    }

    private UserProfile getUserProfileByCurrentUser(){
        String userId = (String) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        return userProfileRepository.findByUserId(Long.parseLong(userId))
                .orElseThrow(() -> new ResourceNotFoundException("Profile not exists"));
    }

}
