package com.lean2708.auth_service.service;

import com.lean2708.auth_service.dto.request.PhoneRequest;
import com.lean2708.auth_service.dto.request.ResetPasswordRequest;
import com.lean2708.auth_service.dto.request.VerifyOtpRequest;
import com.lean2708.auth_service.dto.response.OtpResponse;
import com.lean2708.auth_service.entity.ForgotPasswordToken;
import com.nimbusds.jose.JOSEException;

public interface AccountRecoveryService {

    OtpResponse forgotPassword(PhoneRequest request);

    ForgotPasswordToken verifyForgotPasswordCode(VerifyOtpRequest request) throws JOSEException;

    void resetPassword(ResetPasswordRequest request);
}
