package com.lean2708.auth_service.service.impl;

import com.lean2708.auth_service.dto.response.PageResponse;
import com.lean2708.auth_service.dto.response.PermissionResponse;
import com.lean2708.auth_service.entity.Permission;
import com.lean2708.auth_service.exception.ResourceNotFoundException;
import com.lean2708.auth_service.mapper.PermissionMapper;
import com.lean2708.auth_service.repository.PermissionRepository;
import com.lean2708.auth_service.service.PermissionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Slf4j(topic = "PERMISSION-SERVICE")
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService {

    private final PermissionRepository permissionRepository;
    private final PermissionMapper permissionMapper;

    @Override
    public PermissionResponse fetchPermissionById(Long id) {
        log.info("Fetch Permission By Id: {}", id);

        Permission permissionDB = permissionRepository.findById(id).
                orElseThrow(() -> new ResourceNotFoundException("Permission not exists"));

        return permissionMapper.toPermissionResponse(permissionDB);
    }

    @Override
    public PageResponse<PermissionResponse> fetchAllPermissions(Pageable pageable) {
        log.info("Fetch All Permission For Admin");

        Page<Permission> permissionPage = permissionRepository.findAll(pageable);

        return PageResponse.<PermissionResponse>builder()
                .page(permissionPage.getNumber())
                .size(permissionPage.getSize())
                .totalPages(permissionPage.getTotalPages())
                .totalItems(permissionPage.getTotalElements())
                .items(permissionMapper.toListPermissionResponse(permissionPage.getContent()))
                .build();
    }
}

