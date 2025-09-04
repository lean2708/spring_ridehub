package com.lean2708.auth_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class RegisterDetailsRequest {

    @NotBlank(message = "Email không được để trống")
    @Pattern(regexp = "^[A-Za-z0-9._%+-]+@gmail\\.com$", message = "Email phải có định dạng hợp lệ")
    private String email;
    private String name;
    private String phone;

}
