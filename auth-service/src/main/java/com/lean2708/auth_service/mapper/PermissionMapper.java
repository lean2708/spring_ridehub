package com.lean2708.auth_service.mapper;

import com.lean2708.auth_service.dto.response.PermissionResponse;
import com.lean2708.auth_service.entity.Permission;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PermissionMapper {

    public PermissionResponse toPermissionResponse(Permission permission){
        return PermissionResponse.builder()
                .id(permission.getId())
                .name(permission.getName())
                .module(permission.getModule())
                .method(permission.getMethod())
                .apiPath(permission.getApiPath())
                .build();
    }


    public List<PermissionResponse> toListPermissionResponse(List<Permission> permissionList){
        return permissionList.stream()
                .map(this::toPermissionResponse)
                .toList();
    }

}
