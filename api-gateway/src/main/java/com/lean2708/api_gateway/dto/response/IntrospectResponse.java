package com.lean2708.api_gateway.dto.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class IntrospectResponse {

    private boolean valid;
    private Long userId;
    private List<String> roles;

}
