package com.lean2708.auth_service.repository;

import com.lean2708.auth_service.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface RoleRepository extends JpaRepository<Role,Long> {

    Optional<Role> findByName(String name);

    @Query("""
    SELECT r FROM Role r
    JOIN UserHasRole uhr ON r.id = uhr.id.roleId
    WHERE uhr.id.userId = :userId""")
    Set<Role> findRolesByUserId(@Param("userId") Long userId);

}

