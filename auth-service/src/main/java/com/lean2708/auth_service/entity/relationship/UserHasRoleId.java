package com.lean2708.auth_service.entity.relationship;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Embeddable
public class UserHasRoleId implements Serializable {

    private Long userId;

    private Long roleId;

}
