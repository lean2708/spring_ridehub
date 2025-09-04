package com.lean2708.auth_service.service;

import com.lean2708.auth_service.dto.response.PageResponse;
import com.lean2708.auth_service.dto.response.PermissionResponse;
import org.springframework.data.domain.Pageable;

public interface PermissionService {

    PermissionResponse fetchPermissionById(Long id);

    PageResponse<PermissionResponse> fetchAllPermissions(Pageable pageable);
}
