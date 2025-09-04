package com.lean2708.auth_service.service;

import com.lean2708.auth_service.dto.response.PageResponse;
import com.lean2708.auth_service.dto.response.PermissionResponse;
import com.lean2708.auth_service.dto.response.RoleResponse;
import org.springframework.data.domain.Pageable;

public interface RoleService {

    RoleResponse fetchRoleById(long id);

    PageResponse<RoleResponse> fetchAllRoles(Pageable pageable);

    PageResponse<PermissionResponse> getPermissionsByRoleId(Pageable pageable, long roleId);

}

