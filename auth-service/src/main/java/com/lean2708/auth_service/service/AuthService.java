package com.lean2708.auth_service.service;

import com.lean2708.auth_service.dto.request.*;
import com.lean2708.auth_service.dto.response.OtpResponse;
import com.lean2708.auth_service.dto.response.TokenResponse;
import com.lean2708.auth_service.dto.response.UserDetailsResponse;
import com.lean2708.auth_service.dto.response.VerifyOtpResponse;
import com.nimbusds.jose.JOSEException;

public interface AuthService {

    TokenResponse login(LoginRequest request) throws JOSEException;

    OtpResponse sendRegistrationOtp(EmailRequest request);

    VerifyOtpResponse verifyOtp(VerifyOtpRequest request);

    UserDetailsResponse addUserDetails(RegisterDetailsRequest request);

    TokenResponse createUserAndSetPassword(SetPasswordRequest request) throws JOSEException;
}
