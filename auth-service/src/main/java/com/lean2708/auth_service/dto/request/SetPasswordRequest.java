package com.lean2708.auth_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class SetPasswordRequest {

    @NotBlank(message = "Email không được để trống")
    @Pattern(regexp = "^[A-Za-z0-9._%+-]+@gmail\\.com$", message = "Email phải có định dạng hợp lệ")
    private String email;

    @Size(min = 6, message = "Password phải từ 6 kí tự trở lên")
    @NotBlank(message = "Password không được để trống")
    private String newPassword;

    @Size(min = 6, message = "confirmPassword phải từ 6 kí tự trở lên")
    @NotBlank(message = "confirmPassword không được để trống")
    private String confirmPassword;
}
