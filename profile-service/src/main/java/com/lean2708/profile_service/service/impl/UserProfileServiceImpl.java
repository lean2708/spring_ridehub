package com.lean2708.profile_service.service.impl;

import com.lean2708.profile_service.dto.request.UserProfileRequest;
import com.lean2708.profile_service.dto.response.UserProfileResponse;
import com.lean2708.profile_service.entity.UserProfile;
import com.lean2708.profile_service.exception.ResourceNotFoundException;
import com.lean2708.profile_service.mapper.ProfileMapper;
import com.lean2708.profile_service.repository.UserProfileRepository;
import com.lean2708.profile_service.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j(topic = "USER-PROFILE-SERVICE")
@Service
@RequiredArgsConstructor
public class UserProfileServiceImpl implements UserProfileService {

    private final UserProfileRepository userProfileRepository;
    private final ProfileMapper profileMapper;

    @Override
    public void createProfile(UserProfileRequest request) {
        log.info("Create profile for userId={}, email={}", request.getUserId(), request.getEmail());

        UserProfile profile = UserProfile.builder()
                .userId(request.getUserId())
                .name(request.getName())
                .email(request.getEmail())
                .build();

         userProfileRepository.save(profile);
    }

    @Override
    public UserProfileResponse getProfileByUserId(Long userId) {
        log.info("Get Profile By userId={}", userId);

        UserProfile userProfile = getUserProfileByUserId(userId);

        return profileMapper.toProfileResponse(userProfile);
    }

    private UserProfile getUserProfileByUserId(Long userId){
        return userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not exists"));
    }
}
