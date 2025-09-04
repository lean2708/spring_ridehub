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

import java.text.ParseException;

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
                .message("OTP has been sent to your email. Please check your inbox.")
                .build();
    }


    @PostMapping("/register/verify")
    public ApiResponse<VerifyOtpResponse> verifyRegistrationOtp(@Valid @RequestBody VerifyOtpRequest request) {
        log.info("Verifying OTP for email: {}", request.getEmail());

        return ApiResponse.<VerifyOtpResponse>builder()
                .code(HttpStatus.OK.value())
                .result(authService.verifyRegistrationOtp(request))
                .message("OTP verification successful")
                .build();
    }


    @PostMapping("/register/details")
    public ApiResponse<UserDetailsResponse> addUserDetails(@Valid @RequestBody RegisterDetailsRequest request) {
        log.info("Adding user details for email: {}", request.getEmail());

        return ApiResponse.<UserDetailsResponse>builder()
                .code(HttpStatus.OK.value())
                .result(authService.addUserDetails(request))
                .message("Your personal information has been temporarily saved. Please set a password to complete registration.")
                .build();
    }


    @PostMapping("/register/set-password")
    public ApiResponse<TokenResponse> setPassword(@Valid @RequestBody SetPasswordRequest request) throws JOSEException {
        log.info("Setting password and creating user for email: {}", request.getEmail());

        return ApiResponse.<TokenResponse>builder()
                .code(HttpStatus.CREATED.value())
                .result(authService.createUserAndSetPassword(request))
                .message("Registration successful.")
                .build();
    }


    @Operation(summary = "Forgot Password",
            description = "API này được sử dụng để quên mật khẩu")
    @PostMapping("/forgot-password")
    public ApiResponse<OtpResponse> forgotPassword(@Valid @RequestBody EmailRequest request) {
        log.info("Received forgot password request for email: {}", request.getEmail());

        return ApiResponse.<OtpResponse>builder()
                .code(HttpStatus.OK.value())
                .result(accountRecoveryService.forgotPassword(request))
                .message("A verification code has been sent to your email. Please check your inbox to complete the password recovery process.")
                .build();
    }

    @PostMapping("/forgot-password/verify")
    public ApiResponse<ForgotPasswordToken> verifyForgotPasswordCode(@Valid @RequestBody VerifyOtpRequest request) throws JOSEException {
        log.info("Received verifying forgot password code for email: {}", request.getEmail());

        return ApiResponse.<ForgotPasswordToken>builder()
                .code(HttpStatus.OK.value())
                .result(accountRecoveryService.verifyForgotPasswordCode(request))
                .message("Verification code is valid.")
                .build();
    }

    @PostMapping("/forgot-password/reset-password")
    public ApiResponse<Void> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        log.info("Received password reset request");

        accountRecoveryService.resetPassword(request);
        return ApiResponse.<Void>builder()
                .code(HttpStatus.OK.value())
                .message("Password has been successfully changed.")
                .build();
    }


    @PostMapping("/refresh-token")
    public ApiResponse<TokenResponse> refreshToken(@Valid @RequestBody RefreshRequest request) throws ParseException, JOSEException {
        log.info("Received refresh token: {}", request.getRefreshToken());

        return ApiResponse.<TokenResponse>builder()
                .code(HttpStatus.OK.value())
                .result(authService.refreshToken(request))
                .message("Refresh Token")
                .build();
    }




}
