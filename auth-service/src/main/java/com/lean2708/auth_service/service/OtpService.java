package com.lean2708.auth_service.service;

import com.lean2708.auth_service.constants.OtpType;
import com.lean2708.auth_service.entity.OtpVerification;

public interface OtpService {

    OtpVerification saveOtp(String email, OtpType type);

    OtpVerification getOtp(String email, OtpType type, String otp);


}
