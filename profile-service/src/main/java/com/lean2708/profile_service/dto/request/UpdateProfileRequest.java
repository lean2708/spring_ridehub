package com.lean2708.profile_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class UpdateProfileRequest {

    @NotBlank(message = "Tên không được để trống")
    private String name;

    @NotBlank(message = "Email không được để trống")
    @Pattern(regexp = "^[A-Za-z0-9._%+-]+@gmail\\.com$", message = "Email phải có định dạng hợp lệ")
    private String email;

    private String phone;

    private LocalDate dateOfBirth;

}
