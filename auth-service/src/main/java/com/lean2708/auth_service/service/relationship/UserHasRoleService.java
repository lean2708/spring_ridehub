package com.lean2708.auth_service.service.relationship;

import com.lean2708.auth_service.constants.RoleEnum;
import com.lean2708.auth_service.entity.Role;
import com.lean2708.auth_service.entity.User;
import com.lean2708.auth_service.entity.relationship.UserHasRole;
import com.lean2708.auth_service.entity.relationship.UserHasRoleId;
import com.lean2708.auth_service.exception.ResourceNotFoundException;
import com.lean2708.auth_service.repository.RoleRepository;
import com.lean2708.auth_service.repository.relationship.UserHasRoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j(topic = "USER-HAS-ROLE-SERVICE")
@Service
@RequiredArgsConstructor
public class UserHasRoleService {

    private final UserHasRoleRepository userHasRoleRepository;
    private final RoleRepository roleRepository;

    public UserHasRole saveUserHasRole(User user, RoleEnum roleEnum){
        Role role = roleRepository.findByName(roleEnum.name())
                .orElseThrow(() -> new ResourceNotFoundException("Role not exists"));

        UserHasRole userHasRole = UserHasRole.builder()
                .id(new UserHasRoleId(user.getId(), role.getId()))
                .build();

        return userHasRoleRepository.save(userHasRole);
    }
}
