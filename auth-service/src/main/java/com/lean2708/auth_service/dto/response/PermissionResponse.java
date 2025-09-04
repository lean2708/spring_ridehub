package com.lean2708.auth_service.dto.response;
import lombok.*;


@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PermissionResponse {

    private Long id;
    private String name;

    private String module;
    private String apiPath;
    private String method;


}

