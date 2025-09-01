package com.lean2708.auth_service.controller;

import com.lean2708.auth_service.dto.request.*;
import com.lean2708.auth_service.dto.response.*;
import com.lean2708.auth_service.entity.ForgotPasswordToken;
import com.lean2708.auth_service.service.AccountRecoveryService;
import com.lean2708.auth_service.service.AuthService;
import com.nimbusds.jose.JOSEException;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j(topic = "AUTH-CONTROLLER")
@RequiredArgsConstructor
@Validated
@RequestMapping("/v1/auth")
@RestController
public class AuthController {

    private final AuthService authService;
    private final AccountRecoveryService accountRecoveryService;


    @PostMapping("/login")
    public ApiResponse<TokenResponse> login(@Valid @RequestBody LoginRequest request) throws JOSEException {
        log.info("Received login request for email: {}", request.getEmail());

        return ApiResponse.<TokenResponse>builder()
                .code(HttpStatus.OK.value())
                .result(authService.login(request))
                .message("Login")
                .build();
    }

    @PostMapping("/register")
    public ApiResponse<OtpResponse> sendRegistrationOtp(@Valid @RequestBody EmailRequest request) throws JOSEException {
        log.info("Received registration request for email: {}", request.getEmail());

        return ApiResponse.<OtpResponse>builder()
                .code(HttpStatus.OK.value())
                .result( authService.sendRegistrationOtp(request))
                .message("OTP đã được gửi đến email. Vui lòng kiểm tra email của bạn")
                .build();
    }


    @PostMapping("/register/verify")
    public ApiResponse<VerifyOtpResponse> verifyOtp(@Valid @RequestBody VerifyOtpRequest request) {
        log.info("Verifying OTP for email: {}", request.getEmail());

        return ApiResponse.<VerifyOtpResponse>builder()
                .code(HttpStatus.OK.value())
                .result(authService.verifyOtp(request))
                .message("Xác thực OTP thành công.")
                .build();
    }


    @PostMapping("/register/details")
    public ApiResponse<UserDetailsResponse> addUserDetails(@Valid @RequestBody RegisterDetailsRequest request) {
        log.info("Adding user details for email: {}", request.getEmail());

        return ApiResponse.<UserDetailsResponse>builder()
                .code(HttpStatus.OK.value())
                .result(authService.addUserDetails(request))
                .message("Thông tin cá nhân đã được lưu tạm. Vui lòng đặt mật khẩu để hoàn tất đăng ký.")
                .build();
    }


    @PostMapping("/register/set-password")
    public ApiResponse<TokenResponse> setPassword(@Valid @RequestBody SetPasswordRequest request) throws JOSEException {
        log.info("Setting password and creating user for email: {}", request.getEmail());

        return ApiResponse.<TokenResponse>builder()
                .code(HttpStatus.CREATED.value())
                .result(authService.createUserAndSetPassword(request))
                .message("Đăng ký thành công.")
                .build();
    }


    @Operation(summary = "Forgot Password",
            description = "API này được sử dụng để quên mật khẩu")
    @PostMapping("/public/auth/forgot-password")
    public ApiResponse<OtpResponse> forgotPassword(@Valid @RequestBody EmailRequest request) {
        log.info("Received forgot password request for email: {}", request.getEmail());

        return ApiResponse.<OtpResponse>builder()
                .code(HttpStatus.OK.value())
                .result(accountRecoveryService.forgotPassword(request))
                .message("Mã xác nhận đã được gửi vào email của bạn. Vui lòng kiểm tra để hoàn tất quá trình lấy lại mật khẩu")
                .build();
    }

    @PostMapping("/public/auth/forgot-password/verify")
    public ApiResponse<ForgotPasswordToken> verifyForgotPasswordCode(@Valid @RequestBody VerifyOtpRequest request) throws JOSEException {
        log.info("Received verifying forgot password code for email: {}", request.getEmail());

        return ApiResponse.<ForgotPasswordToken>builder()
                .code(HttpStatus.OK.value())
                .result(accountRecoveryService.verifyForgotPasswordCode(request))
                .message("Mã xác nhận hợp lệ")
                .build();
    }

    @PostMapping("/public/auth/forgot-password/reset-password")
    public ApiResponse<Void> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        log.info("Received password reset request");

        accountRecoveryService.resetPassword(request);
        return ApiResponse.<Void>builder()
                .code(HttpStatus.OK.value())
                .message("Mật khẩu đã được thay đổi thành công")
                .build();
    }




}
