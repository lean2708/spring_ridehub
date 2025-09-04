package com.lean2708.auth_service.mapper;

import com.lean2708.auth_service.dto.response.RoleResponse;
import com.lean2708.auth_service.entity.Role;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RoleMapper {

    public RoleResponse toRoleResponse(Role role){
        return RoleResponse.builder()
                .id(role.getId())
                .name(role.getName())
                .description(role.getDescription())
                .build();
    }

    public List<RoleResponse> toListRoleResponse(List<Role> roleList){
        return roleList.stream()
                .map(this::toRoleResponse)
                .toList();
    }
}
