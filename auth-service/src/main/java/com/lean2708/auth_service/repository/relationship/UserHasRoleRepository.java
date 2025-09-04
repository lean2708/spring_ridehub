package com.lean2708.auth_service.repository.relationship;

import com.lean2708.auth_service.entity.relationship.UserHasRole;
import com.lean2708.auth_service.entity.relationship.UserHasRoleId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserHasRoleRepository extends JpaRepository<UserHasRole, UserHasRoleId> {
}
