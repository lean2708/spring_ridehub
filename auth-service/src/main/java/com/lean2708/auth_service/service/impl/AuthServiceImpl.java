package com.lean2708.auth_service.service.impl;

import com.lean2708.auth_service.constants.EntityStatus;
import com.lean2708.auth_service.constants.OtpType;
import com.lean2708.auth_service.constants.RegistrationStatus;
import com.lean2708.auth_service.constants.TokenType;
import com.lean2708.auth_service.dto.basic.EntityBasic;
import com.lean2708.auth_service.dto.event.EmailEvent;
import com.lean2708.auth_service.dto.request.*;
import com.lean2708.auth_service.dto.response.OtpResponse;
import com.lean2708.auth_service.dto.response.TokenResponse;
import com.lean2708.auth_service.dto.response.UserDetailsResponse;
import com.lean2708.auth_service.dto.response.VerifyOtpResponse;
import com.lean2708.auth_service.entity.OtpVerification;
import com.lean2708.auth_service.entity.Role;
import com.lean2708.auth_service.entity.User;
import com.lean2708.auth_service.entity.UserRegistration;
import com.lean2708.auth_service.exception.InvalidDataException;
import com.lean2708.auth_service.exception.ResourceNotFoundException;
import com.lean2708.auth_service.exception.UnauthenticatedException;
import com.lean2708.auth_service.repository.OtpVerificationRepository;
import com.lean2708.auth_service.repository.RoleRepository;
import com.lean2708.auth_service.repository.UserRegistrationRepository;
import com.lean2708.auth_service.repository.UserRepository;
import com.lean2708.auth_service.service.AuthService;
import com.lean2708.auth_service.service.OtpService;
import com.lean2708.auth_service.service.TokenService;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.Set;
import java.util.stream.Collectors;

import static com.lean2708.auth_service.constants.RegistrationStatus.*;


@Service
@Slf4j(topic = "AUTH-SERVICE")
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;
    private final OtpService otpService;
    private final RoleRepository roleRepository;
    private final UserRegistrationRepository userRegistrationRepository;
    private final OtpVerificationRepository otpVerificationRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public TokenResponse login(LoginRequest request) throws JOSEException {
        log.info("Handling login for email: {}", request.getEmail());

        User userDB = userRepository.findByEmail(request.getEmail())
                .orElseThrow(()-> new ResourceNotFoundException("User not exists"));

        boolean isAuthenticated = passwordEncoder.matches(request.getPassword(), userDB.getPassword());

        if(!isAuthenticated){
            throw new UnauthenticatedException("Password is incorrect");
        }
        return generateAndSaveTokenResponse(userDB);
    }

    @Override
    public OtpResponse sendRegistrationOtp(EmailRequest request) {
        log.info("Send registration OTP for email: {}", request.getEmail());

        UserRegistration userRegistration = UserRegistration.builder()
                .email(request.getEmail())
                .status(RegistrationStatus.PENDING)
                .build();

        OtpVerification otpVerification = otpService.saveOtp(request.getEmail(), OtpType.REGISTER);

        userRegistration.setTtl(otpVerification.getTtl());
        userRegistrationRepository.save(userRegistration);

         // Kafka
        kafkaTemplate.send("email-register", EmailEvent.builder()
                .toEmail(userRegistration.getEmail())
                .name(userRegistration.getName())
                .otp(otpVerification.getOtp())
                .build());

        return OtpResponse.builder()
                .email(request.getEmail())
                .otp(otpVerification.getOtp())
                .build();
    }

    @Override
    public VerifyOtpResponse verifyRegistrationOtp(VerifyOtpRequest request) {
        log.info("Verifying OTP {} for email: {}", request.getOtp(), request.getEmail());

        OtpVerification otpVerification = otpService.getOtp(request.getEmail(), OtpType.REGISTER, request.getOtp());

        UserRegistration userRegistration = getUserRegistration(request.getEmail());

        updateStatusRegistration(userRegistration, OTP_VERIFIED);

        // delete OTP
        otpVerificationRepository.delete(otpVerification);

        return VerifyOtpResponse.builder()
                .email(request.getEmail())
                .build();
    }

    @Override
    public UserDetailsResponse addUserDetails(RegisterDetailsRequest request) {
        log.info("Adding user details for email: {}", request.getEmail());

        UserRegistration userRegistration = getUserRegistration(request.getEmail());

        userRegistration.setName(request.getName());
        userRegistration.setPhone(request.getPhone());

        updateStatusRegistration(userRegistration, DETAILS_ADDED);

        return UserDetailsResponse.builder()
                .email(request.getEmail())
                .name(request.getName())
                .phone(request.getPhone())
                .status(userRegistration.getStatus())
                .build();
    }

    @Override
    public TokenResponse createUserAndSetPassword(SetPasswordRequest request) throws JOSEException {
        log.info("Creating user and setting password for email: {}", request.getEmail());

        if(!request.getNewPassword().equals(request.getConfirmPassword())){
            throw new InvalidDataException("Password and Confirm Password do not match");
        }

        UserRegistration userRegistration = getUserRegistration(request.getEmail());

        updateStatusRegistration(userRegistration, COMPLETED);

        User user = User.builder()
                .email(userRegistration.getEmail())
                .phone(userRegistration.getPhone())
                .password(passwordEncoder.encode(request.getNewPassword()))
                .status(EntityStatus.ACTIVE)
                .build();

        userRepository.save(user);
//        userHasRoleService.saveUserHasRole(user, RoleEnum.USER);

        userRegistrationRepository.delete(userRegistration);

        return generateAndSaveTokenResponse(user);
    }

    @Override
    public TokenResponse refreshToken(RefreshRequest request) throws ParseException, JOSEException {
        log.info("Refreshing token");

        // verify refresh token (db, expirationTime ...)
        SignedJWT signedJWT = tokenService.verifyToken(request.getRefreshToken(), TokenType.REFRESH_TOKEN);

        String email = signedJWT.getJWTClaimsSet().getSubject();
        User user = getUserByEmail(email);

        Set<Role> roleSet = roleRepository.findRolesByUserId(user.getId());

        Set<EntityBasic> roleBasic = roleSet.stream()
                .map(role -> new EntityBasic(role.getId(), role.getName()))
                .collect(Collectors.toSet());

        // new access token
        String accessToken = tokenService.generateToken(user, TokenType.ACCESS_TOKEN);

        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(request.getRefreshToken())
                .email(email)
                .roles(roleBasic)
                .build();
    }


    private TokenResponse generateAndSaveTokenResponse(User user) throws JOSEException {
        String accessToken = tokenService.generateToken(user, TokenType.ACCESS_TOKEN);

        String refreshToken = tokenService.generateToken(user, TokenType.REFRESH_TOKEN);

        tokenService.saveRefreshToken(refreshToken);

        Set<Role> roleSet = roleRepository.findRolesByUserId(user.getId());

        Set<EntityBasic> roleBasic = roleSet.stream()
                .map(role -> new EntityBasic(role.getId(), role.getName()))
                .collect(Collectors.toSet());

        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .authenticated(true)
                .email(user.getEmail())
                .roles(roleBasic)
                .build();
    }

    private void updateStatusRegistration(UserRegistration userRegistration, RegistrationStatus newStatus){
        userRegistration.getStatus().validateTransition(newStatus);

        userRegistration.setStatus(newStatus);

        userRegistrationRepository.save(userRegistration);
    }


    private UserRegistration getUserRegistration(String id){
        return userRegistrationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Registration session not found"));
    }

    private User getUserByEmail(String email){
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not exists"));
    }

}
