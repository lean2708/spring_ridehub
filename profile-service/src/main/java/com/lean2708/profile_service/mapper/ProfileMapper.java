package com.lean2708.profile_service.mapper;

import com.lean2708.profile_service.dto.response.UserProfileResponse;
import com.lean2708.profile_service.entity.UserProfile;
import org.springframework.stereotype.Component;

@Component
public class ProfileMapper {

    public UserProfileResponse toProfileResponse(UserProfile userProfile){
        return UserProfileResponse.builder()
                .id(userProfile.getId())
                .userId(userProfile.getUserId())
                .phone(userProfile.getName())
                .name(userProfile.getName())
                .email(userProfile.getEmail())
                .avatarUrl(userProfile.getAvatarUrl())
                .dateOfBirth(userProfile.getDateOfBirth())
                .createdAt(userProfile.getCreatedAt())
                .build();
    }
}
