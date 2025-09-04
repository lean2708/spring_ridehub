package com.lean2708.auth_service.entity.relationship;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;


@Getter
@Setter
@Table(name = "user_has_role")
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
public class UserHasRole {

    @EmbeddedId
    private UserHasRoleId id;



}

