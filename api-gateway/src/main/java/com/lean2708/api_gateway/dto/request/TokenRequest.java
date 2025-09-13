package com.lean2708.api_gateway.dto.request;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TokenRequest {

    @NotBlank(message = "accessToken không được để trống")
    private String accessToken;
}

