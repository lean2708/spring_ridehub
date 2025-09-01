package com.lean2708.auth_service.service.impl;
import com.lean2708.auth_service.constants.OtpType;
import com.lean2708.auth_service.entity.OtpVerification;
import com.lean2708.auth_service.exception.InvalidDataException;
import com.lean2708.auth_service.exception.ResourceNotFoundException;
import com.lean2708.auth_service.repository.OtpVerificationRepository;
import com.lean2708.auth_service.service.OtpService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j(topic = "OTP-SERVICE")
@RequiredArgsConstructor
public class OtpServiceImpl implements OtpService {

    private final OtpVerificationRepository otpVerificationRepository;

    @Value("${auth.register.verification-code.expiration-minutes}")
    private long registerExpiration;

    @Value("${auth.forgot-password.verification-code.expiration-minutes}")
    private long forgotPasswordExpiration;


    public OtpVerification saveOtp(String email, OtpType type){
        String otp = generateVerificationCode();

        String redisKey = email + ":" + type;

        OtpVerification otpVerification = OtpVerification.builder()
                .redisKey(redisKey)
                .email(email)
                .otp(otp)
                .otpType(type)
                .ttl(getExpirationTimeByType(type) * 60)
                .build();

        return otpVerificationRepository.save(otpVerification);
    }

    public OtpVerification getOtp(String email, OtpType type, String otp){
        String redisKey = email + ":" + type;

        OtpVerification otpVerification = getOtpVerification(redisKey);

        if (!otpVerification.getOtp().equals(otp)) {
            throw new InvalidDataException("OTP is incorrect");
        }

        return otpVerification;
    }


    private Long getExpirationTimeByType(OtpType type){
        switch (type){
            case REGISTER -> {
                return registerExpiration;
            }
            case FORGOT_PASSWORD -> {
                return forgotPasswordExpiration;
            }
            default -> throw new InvalidDataException("Invalid Otp Type");
        }
    }

    private OtpVerification getOtpVerification(String id){
        return otpVerificationRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("OTP not exists"));
    }


    private String generateVerificationCode() {
        return String.format("%06d", (int) (Math.random() * 1000000));
    }

}
