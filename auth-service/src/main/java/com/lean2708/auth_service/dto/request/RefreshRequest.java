package com.lean2708.auth_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;


@Getter
public class RefreshRequest {

    @NotBlank(message = "refreshToken không được để trống")
    private String refreshToken;


}
