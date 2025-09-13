package com.lean2708.auth_service.dto.request;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class TokenRequest {

    @NotBlank(message = "accessToken không được để trống")
    private String accessToken;
}

