package com.lean2708.auth_service.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoleResponse {

    private Long id;
    private String name;
    private String description;



}
