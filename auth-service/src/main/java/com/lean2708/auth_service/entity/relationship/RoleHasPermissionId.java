package com.lean2708.auth_service.entity.relationship;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class RoleHasPermissionId implements Serializable {

    private Long roleId;

    private Long permissionId;

}
