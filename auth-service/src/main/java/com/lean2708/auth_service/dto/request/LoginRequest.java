package com.lean2708.auth_service.dto.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class LoginRequest {

    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "^(\\+84)\\d{9}$", message = "Số điện thoại phải có định dạng hợp lệ")
    private String phone;

    @Size(min = 6, message = "Password phải từ 6 kí tự trở lên")
    @NotBlank(message = "Password không được để trống")
    private String password;
}
