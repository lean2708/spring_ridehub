package com.lean2708.auth_service.service.impl;

import com.lean2708.auth_service.constants.OtpType;
import com.lean2708.auth_service.constants.TokenType;
import com.lean2708.auth_service.dto.event.SmsEvent;
import com.lean2708.auth_service.dto.request.PhoneRequest;
import com.lean2708.auth_service.dto.request.ResetPasswordRequest;
import com.lean2708.auth_service.dto.request.VerifyOtpRequest;
import com.lean2708.auth_service.dto.response.OtpResponse;
import com.lean2708.auth_service.entity.ForgotPasswordToken;
import com.lean2708.auth_service.entity.OtpVerification;
import com.lean2708.auth_service.entity.User;
import com.lean2708.auth_service.repository.ForgotPasswordTokenRepository;
import com.lean2708.auth_service.repository.OtpVerificationRepository;
import com.lean2708.auth_service.repository.UserRepository;
import com.lean2708.auth_service.service.AccountRecoveryService;
import com.lean2708.auth_service.service.OtpService;
import com.lean2708.auth_service.service.TokenService;
import com.lean2708.common_library.exception.InvalidDataException;
import com.lean2708.common_library.exception.ResourceNotFoundException;
import com.nimbusds.jose.JOSEException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.BadJwtException;
import org.springframework.stereotype.Service;

import java.text.ParseException;

@Service
@Slf4j(topic = "ACCOUNT-RECOVERY-SERVICE")
@RequiredArgsConstructor
public class AccountRecoveryServiceImpl implements AccountRecoveryService {

    private final OtpService otpService;
    private final UserRepository userRepository;
    private final TokenService tokenService;
    private final OtpVerificationRepository otpVerificationRepository;
    private final ForgotPasswordTokenRepository forgotPasswordTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${jwt.reset.expiry-in-minutes}")
    private long resetTokenExpiration;


    @Override
    public OtpResponse forgotPassword(PhoneRequest request) {
        log.info("Forgot password requested for phone: {}", request.getPhone());

        User user = getUserByPhone(request.getPhone());

        OtpVerification otpVerification = otpService.saveOtp(request.getPhone(), OtpType.FORGOT_PASSWORD);

         // Kafka
        kafkaTemplate.send("reset-password-sms-events", SmsEvent.builder()
                .toPhone(user.getPhone())
                .otp(otpVerification.getOtp())
                .build());

        return OtpResponse.builder()
                .phone(otpVerification.getPhone())
                .otp(otpVerification.getOtp())
                .build();

    }


    @Override
    public ForgotPasswordToken verifyForgotPasswordCode(VerifyOtpRequest request) throws JOSEException {
        log.info("Verifying forgot password code for phone: {}", request.getPhone());

        OtpVerification otpVerification = otpService.getOtp(request.getPhone(), OtpType.FORGOT_PASSWORD, request.getOtp());

        User user = getUserByPhone(request.getPhone());

        String forgotPasswordToken = tokenService.generateToken(user, TokenType.RESET_PASSWORD_TOKEN);

        ForgotPasswordToken token = ForgotPasswordToken.builder()
                .forgotPasswordToken(forgotPasswordToken)
                .phone(request.getPhone())
                .ttl(resetTokenExpiration * 60)
                .build();

        otpVerificationRepository.delete(otpVerification);

        return forgotPasswordTokenRepository.save(token);
    }


    @Override
    public void resetPassword(ResetPasswordRequest request) {
        log.info("Resetting password");

        try {
            tokenService.verifyToken(request.getForgotPasswordToken(), TokenType.RESET_PASSWORD_TOKEN);
        } catch (JOSEException | ParseException e) {
            throw new BadJwtException(e.getMessage());
        }

        ForgotPasswordToken forgotPasswordToken = forgotPasswordTokenRepository
                .findById(request.getForgotPasswordToken())
                .orElseThrow( () -> new ResourceNotFoundException("Forgot Password Token not found"));

        User user = getUserByPhone(forgotPasswordToken.getPhone());

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new InvalidDataException("Password and Confirm Password do not match");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        forgotPasswordTokenRepository.delete(forgotPasswordToken);
    }


    private User getUserByPhone(String phone){
        return userRepository.findByPhone(phone)
                .orElseThrow(() -> new ResourceNotFoundException("User not exists"));
    }


}
