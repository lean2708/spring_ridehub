package com.lean2708.auth_service.service;

import com.lean2708.auth_service.dto.request.*;
import com.lean2708.auth_service.dto.response.*;
import com.nimbusds.jose.JOSEException;
import jakarta.validation.Valid;

import java.text.ParseException;

public interface AuthService {

    TokenResponse login(LoginRequest request) throws JOSEException;

    OtpResponse sendRegistrationOtp(PhoneRequest request);

    VerifyOtpResponse verifyRegistrationOtp(VerifyOtpRequest request);

    UserDetailsResponse addUserDetails(RegisterDetailsRequest request);

    TokenResponse createUserAndSetPassword(SetPasswordRequest request) throws JOSEException;

    TokenResponse refreshToken(RefreshRequest request) throws ParseException, JOSEException;

    void changePassword(ChangePasswordRequest request);

    void logout(TokenRequest request) throws ParseException, JOSEException;

    IntrospectResponse introspect(TokenRequest request);
}
