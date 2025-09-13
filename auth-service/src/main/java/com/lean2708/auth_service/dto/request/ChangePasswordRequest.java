package com.lean2708.auth_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class ChangePasswordRequest {

    @NotBlank(message = "oldPassword không được để trống")
    @Size(min = 6, message = "oldPassword phải từ 6 kí tự trở lên")
    private String oldPassword;

    @NotBlank(message = "newPassword không được để trống")
    @Size(min = 6, message = "newPassword phải từ 6 kí tự trở lên")
    private String newPassword;

    @Size(min = 6, message = "confirmPassword phải từ 6 kí tự trở lên")
    @NotBlank(message = "confirmPassword không được để trống")
    private String confirmPassword;
}

