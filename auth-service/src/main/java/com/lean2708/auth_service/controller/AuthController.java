package com.lean2708.auth_service.controller;

import com.lean2708.auth_service.dto.request.*;
import com.lean2708.auth_service.dto.response.*;
import com.lean2708.auth_service.entity.ForgotPasswordToken;
import com.lean2708.auth_service.service.AccountRecoveryService;
import com.lean2708.auth_service.service.AuthService;
import com.lean2708.common_library.dto.response.ApiResponse;
import com.nimbusds.jose.JOSEException;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;

@Slf4j(topic = "AUTH-CONTROLLER")
@RequiredArgsConstructor
@Validated
@RestController
public class AuthController {

    private final AuthService authService;
    private final AccountRecoveryService accountRecoveryService;


    @Operation(summary = "Đăng nhập",
            description = "API cho phép người dùng đăng nhập bằng số điện thoại  và mật khẩu để nhận token.")
    @PostMapping("/login")
    public ApiResponse<TokenResponse> login(@Valid @RequestBody LoginRequest request) throws JOSEException {
        log.info("Received login request for phone: {}", request.getPhone());

        return ApiResponse.<TokenResponse>builder()
                .code(HttpStatus.OK.value())
                .result(authService.login(request))
                .message("Login")
                .build();
    }

    @Operation(summary = "Gửi OTP đăng ký",
            description = "Bước 1: API gửi mã OTP tới số điện thoại người dùng để bắt đầu quá trình đăng ký")
    @PostMapping("/register")
    public ApiResponse<OtpResponse> sendRegistrationOtp(@Valid @RequestBody PhoneRequest request) throws JOSEException {
        log.info("Received registration request for phone: {}", request.getPhone());

        return ApiResponse.<OtpResponse>builder()
                .code(HttpStatus.OK.value())
                .result( authService.sendRegistrationOtp(request))
                .message("OTP has been sent to your phone. Please check your inbox.")
                .build();
    }


    @Operation(summary = "Xác thực OTP đăng ký",
            description = "Bước 2: API xác thực mã OTP đã gửi tới email trong quá trình đăng ký.")
    @PostMapping("/register/verify")
    public ApiResponse<VerifyOtpResponse> verifyRegistrationOtp(@Valid @RequestBody VerifyOtpRequest request) {
        log.info("Verifying OTP for phone: {}", request.getPhone());

        return ApiResponse.<VerifyOtpResponse>builder()
                .code(HttpStatus.OK.value())
                .result(authService.verifyRegistrationOtp(request))
                .message("OTP verification successful")
                .build();
    }

    @Operation(summary = "Thêm thông tin cá nhân",
            description = "Bước 3: API lưu tạm thông tin cá nhân của người dùng (họ tên, số điện thoại) trước khi thiết lập mật khẩu.")
    @PostMapping("/register/details")
    public ApiResponse<UserDetailsResponse> addUserDetails(@Valid @RequestBody RegisterDetailsRequest request) {
        log.info("Adding user details for phone: {}", request.getPhone());

        return ApiResponse.<UserDetailsResponse>builder()
                .code(HttpStatus.OK.value())
                .result(authService.addUserDetails(request))
                .message("Your personal information has been temporarily saved. Please set a password to complete registration.")
                .build();
    }

    @Operation(summary = "Đặt mật khẩu và hoàn tất đăng ký",
            description = "Bước 4: API tạo tài khoản người dùng và thiết lập mật khẩu dựa trên thông tin đã xác thực trước đó.")
    @PostMapping("/register/set-password")
    public ApiResponse<TokenResponse> setPassword(@Valid @RequestBody SetPasswordRequest request) throws JOSEException {
        log.info("Setting password and creating user for phone: {}", request.getPhone());

        return ApiResponse.<TokenResponse>builder()
                .code(HttpStatus.CREATED.value())
                .result(authService.createUserAndSetPassword(request))
                .message("Registration successful.")
                .build();
    }



    @Operation(summary = "Quên mật khẩu",
            description = "Bước 1: API gửi mã OTP tới email để bắt đầu quá trình khôi phục mật khẩu.")
    @PostMapping("/forgot-password")
    public ApiResponse<OtpResponse> forgotPassword(@Valid @RequestBody PhoneRequest request) {
        log.info("Received forgot password request for phone: {}", request.getPhone());

        return ApiResponse.<OtpResponse>builder()
                .code(HttpStatus.OK.value())
                .result(accountRecoveryService.forgotPassword(request))
                .message("A verification code has been sent to your phone. Please check your inbox to complete the password recovery process.")
                .build();
    }

    @Operation(summary = "Xác thực mã OTP quên mật khẩu",
            description = "Bước 2: API xác thực mã OTP trong quá trình khôi phục mật khẩu.")
    @PostMapping("/forgot-password/verify")
    public ApiResponse<ForgotPasswordToken> verifyForgotPasswordCode(@Valid @RequestBody VerifyOtpRequest request) throws JOSEException {
        log.info("Received verifying forgot password code for phone: {}", request.getPhone());

        return ApiResponse.<ForgotPasswordToken>builder()
                .code(HttpStatus.OK.value())
                .result(accountRecoveryService.verifyForgotPasswordCode(request))
                .message("Verification code is valid.")
                .build();
    }

    @Operation(summary = "Đặt lại mật khẩu",
            description = "Bước 3: API đặt lại mật khẩu mới cho người dùng sau khi xác thực OTP thành công.")
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

    @Operation(summary = "Change Password",
            description = "API này được sử dụng để thay đổi password khi user đã đăng nhập")
    @PutMapping("/change-password")
    public ApiResponse<Void> changePassword(@Valid @RequestBody ChangePasswordRequest request){
        log.info("Received change password request for user");

        authService.changePassword(request);
        return ApiResponse.<Void>builder()
                .code(HttpStatus.OK.value())
                .message("Password changed successfully")
                .build();
    }


    @PostMapping("/logout")
    public ApiResponse<Void> logout(@Valid @RequestBody TokenRequest request) throws JOSEException, ParseException {
        log.info("Received logout request");

        authService.logout(request);
        return ApiResponse.<Void>builder()
                .code(HttpStatus.OK.value())
                .message("Logout")
                .build();
    }


    @Operation(summary = "Introspect token")
    @PostMapping("/introspect")
    public ApiResponse<IntrospectResponse> introspect(@Valid @RequestBody TokenRequest request) throws JOSEException, ParseException {
        log.info("Received introspect request for token");

        return ApiResponse.<IntrospectResponse>builder()
                .code(HttpStatus.OK.value())
                .result(authService.introspect(request))
                .message("Introspect successful")
                .build();
    }




}
