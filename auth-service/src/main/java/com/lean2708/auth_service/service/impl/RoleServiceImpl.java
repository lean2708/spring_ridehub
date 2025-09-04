package com.lean2708.auth_service.service.impl;

import com.lean2708.auth_service.dto.response.PageResponse;
import com.lean2708.auth_service.dto.response.PermissionResponse;
import com.lean2708.auth_service.dto.response.RoleResponse;
import com.lean2708.auth_service.entity.Permission;
import com.lean2708.auth_service.entity.Role;
import com.lean2708.auth_service.exception.ResourceNotFoundException;
import com.lean2708.auth_service.mapper.PermissionMapper;
import com.lean2708.auth_service.mapper.RoleMapper;
import com.lean2708.auth_service.repository.PermissionRepository;
import com.lean2708.auth_service.repository.RoleRepository;
import com.lean2708.auth_service.service.RoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Slf4j(topic = "ROLE-SERVICE")
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;
    private final PermissionRepository permissionRepository;
    private final PermissionMapper permissionMapper;


    @Override
    public RoleResponse fetchRoleById(long id) {
        log.info("Fetch Role By Id: {}", id);

        Role roleDB = findRoleById(id);

        return roleMapper.toRoleResponse(roleDB);
    }

    @Override
    public PageResponse<RoleResponse> fetchAllRoles(Pageable pageable) {
        Page<Role> rolePage = roleRepository.findAll(pageable);

        return PageResponse.<RoleResponse>builder()
                .page(rolePage.getNumber())
                .size(rolePage.getSize())
                .totalPages(rolePage.getTotalPages())
                .totalItems(rolePage.getTotalElements())
                .items(roleMapper.toListRoleResponse(rolePage.getContent()))
                .build();
    }

    @Override
    public PageResponse<PermissionResponse> getPermissionsByRoleId(Pageable pageable, long roleId) {
        Page<Permission> permissionPage = permissionRepository.findAllByRoleId(roleId, pageable);

        return PageResponse.<PermissionResponse>builder()
                .page(permissionPage.getNumber())
                .size(permissionPage.getSize())
                .totalPages(permissionPage.getTotalPages())
                .totalItems(permissionPage.getTotalElements())
                .items(permissionMapper.toListPermissionResponse(permissionPage.getContent()))
                .build();
    }


    private Role findRoleById(long id) {
        return roleRepository.findById(id).
                orElseThrow(() -> new ResourceNotFoundException("Role not exists"));
    }
}
