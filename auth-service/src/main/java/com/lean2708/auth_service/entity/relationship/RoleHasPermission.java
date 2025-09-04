package com.lean2708.auth_service.entity.relationship;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@Table(name = "role_has_permission")
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
public class RoleHasPermission {

    @Id
    @EmbeddedId
    private RoleHasPermissionId id;


}
