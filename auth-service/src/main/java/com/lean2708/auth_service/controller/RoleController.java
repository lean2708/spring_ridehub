package com.lean2708.auth_service.controller;
import com.lean2708.auth_service.dto.response.ApiResponse;
import com.lean2708.auth_service.dto.response.PageResponse;
import com.lean2708.auth_service.dto.response.PermissionResponse;
import com.lean2708.auth_service.dto.response.RoleResponse;
import com.lean2708.auth_service.service.RoleService;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Slf4j(topic = "ROLE-CONTROLLER")
@RequiredArgsConstructor
@Validated
@RequestMapping("/v1")
@RestController
public class RoleController {

    private final RoleService roleService;


    @GetMapping("/roles/{id}")
    public ApiResponse<RoleResponse> fetchRoleById(@Min(value = 1, message = "ID phải lớn hơn 0")
                                                   @PathVariable long id){
        log.info("Received request to fetch role by id: {}", id);

        return ApiResponse.<RoleResponse>builder()
                .code(HttpStatus.OK.value())
                .message("Fetch Role By Id")
                .result(roleService.fetchRoleById(id))
                .build();
    }


    @GetMapping("/roles")
    public ApiResponse<PageResponse<RoleResponse>> fetchAll(@ParameterObject @PageableDefault Pageable pageable){
        log.info("Received request to fetch all roles for admin");

        return ApiResponse.<PageResponse<RoleResponse>>builder()
                .code(HttpStatus.OK.value())
                .result(roleService.fetchAllRoles(pageable))
                .message("Fetch All Roles With Pagination")
                .build();
    }


    @GetMapping("/roles/{roleId}/permissions")
    public ApiResponse<PageResponse<PermissionResponse>> getPermissionsByRole(@ParameterObject @PageableDefault Pageable pageable,
                                                                              @PathVariable long roleId) {
        log.info("Received request to fetch all permissions by Role");

        return ApiResponse.<PageResponse<PermissionResponse>>builder()
                .code(HttpStatus.OK.value())
                .message("Fetch Permissions by Role Id")
                .result(roleService.getPermissionsByRoleId(pageable, roleId))
                .build();
    }

}

