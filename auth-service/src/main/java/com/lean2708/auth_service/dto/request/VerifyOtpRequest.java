package com.lean2708.auth_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;


@Getter
public class VerifyOtpRequest {

    @NotBlank(message = "Email không được để trống")
    @Pattern(regexp = "^[A-Za-z0-9._%+-]+@gmail\\.com$", message = "Email phải có định dạng hợp lệ")
    private String email;

    @NotBlank(message = "OTP không được để trống")
    @Pattern(regexp = "^[0-9]{6}$", message = "OTP phải gồm đúng 6 chữ số")
    private String otp;

}
