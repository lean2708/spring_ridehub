package com.lean2708.auth_service.config;

import com.lean2708.auth_service.constants.EntityStatus;
import com.lean2708.auth_service.constants.RoleEnum;
import com.lean2708.auth_service.dto.request.UserProfileRequest;
import com.lean2708.auth_service.entity.Role;
import com.lean2708.auth_service.entity.User;
import com.lean2708.auth_service.repository.RoleRepository;
import com.lean2708.auth_service.repository.UserRepository;
import com.lean2708.auth_service.repository.httpclient.ProfileClient;
import com.lean2708.auth_service.service.relationship.UserHasRoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Slf4j(topic = "DATA-INITIALIZATION")
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserHasRoleService userHasRoleService;
    private final ProfileClient profileClient;

    @Value("${admin.phone}")
    private String adminPhone;

    @Value("${admin.name}")
    private String adminName;

    @Value("${admin.email}")
    private String adminEmail;

    @Value("${admin.password}")
    private String adminPassword;


    @Override
    public void run(String... args) {
        log.info("INIT APPLICATION STARTING....");
        try {
            if (roleRepository.count() == 0) {
                initRoles();
            }

            if (!userRepository.findByEmail(adminEmail).isPresent()) {
                initAdmin();
            }

            log.info("INIT APPLICATION FINISHED SUCCESSFULLY");
        }catch (Exception e) {
            log.error("Cannot create admin profile on startup", e);
        }



    }

    @Transactional
    public void initRoles() {
        log.info("Received request to initialize roles");

        Role userRole = roleRepository.save(Role.builder()
                .name(RoleEnum.USER.name())
                .description("ROLE_USER")
                .build());

        Role adminRole = roleRepository.save(Role.builder()
                .name(RoleEnum.ADMIN.name())
                .description("ROLE_ADMIN")
                .build());

        List<Role> roleList = List.of(userRole, adminRole);
        roleRepository.saveAllAndFlush(roleList);

        log.info("Successfully initialized roles: {}", roleList.size());
    }

    @Transactional
    public void initAdmin() {
        log.info("Received request to initialize admin user.");

        User admin = userRepository.save(User.builder()
                .phone(adminPhone)
                .email(adminEmail)
                .name(adminName)
                .password(passwordEncoder.encode(adminPassword))
                .status(EntityStatus.ACTIVE)
                .build());

        userHasRoleService.saveUserHasRole(admin, RoleEnum.ADMIN);

        // create profile
        profileClient.createProfile(UserProfileRequest.builder()
                .userId(admin.getId())
                .name(admin.getName())
                .phone(admin.getPhone())
                .email(admin.getEmail())
                .build());

        log.info("Successfully initialized admin user with email: {}", admin.getEmail());
    }
}