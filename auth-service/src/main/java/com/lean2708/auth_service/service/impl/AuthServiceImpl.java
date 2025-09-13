package com.lean2708.auth_service.service.impl;

import com.lean2708.auth_service.constants.*;
import com.lean2708.auth_service.dto.event.SmsEvent;
import com.lean2708.auth_service.dto.request.*;
import com.lean2708.auth_service.dto.response.*;
import com.lean2708.auth_service.entity.*;
import com.lean2708.auth_service.repository.OtpVerificationRepository;
import com.lean2708.auth_service.repository.RoleRepository;
import com.lean2708.auth_service.repository.UserRegistrationRepository;
import com.lean2708.auth_service.repository.UserRepository;
import com.lean2708.auth_service.repository.httpclient.ProfileClient;
import com.lean2708.auth_service.service.AuthService;
import com.lean2708.auth_service.service.OtpService;
import com.lean2708.auth_service.service.RevokedTokenService;
import com.lean2708.auth_service.service.TokenService;
import com.lean2708.auth_service.service.relationship.UserHasRoleService;
import com.lean2708.common_library.dto.basic.EntityBasic;
import com.lean2708.common_library.exception.ForBiddenException;
import com.lean2708.common_library.exception.InvalidDataException;
import com.lean2708.common_library.exception.ResourceNotFoundException;
import com.lean2708.common_library.exception.UnauthenticatedException;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
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
    private final UserHasRoleService userHasRoleService;
    private final ProfileClient profileClient;
    private final RevokedTokenService revokedTokenService;
    private final KafkaTemplate<String, Object> kafkaTemplate;


    @Override
    public TokenResponse login(LoginRequest request) throws JOSEException {
        log.info("Handling login for phone: {}", request.getPhone());

        User userDB = userRepository.findByPhone(request.getPhone())
                .orElseThrow(()-> new ResourceNotFoundException("User not exists"));

        boolean isAuthenticated = passwordEncoder.matches(request.getPassword(), userDB.getPassword());

        if(!isAuthenticated){
            throw new UnauthenticatedException("Password is incorrect");
        }
        return generateAndSaveTokenResponse(userDB);
    }


    @Override
    public OtpResponse sendRegistrationOtp(PhoneRequest request) {
        log.info("Send registration OTP for phone: {}", request.getPhone());

        if(userRepository.existsByPhone(request.getPhone())){
            throw new InvalidDataException("User exists");
        }

        UserRegistration userRegistration = UserRegistration.builder()
                .phone(request.getPhone())
                .status(RegistrationStatus.PENDING)
                .build();

        OtpVerification otpVerification = otpService.saveOtp(request.getPhone(), OtpType.REGISTER);

        userRegistration.setTtl(otpVerification.getTtl());
        userRegistrationRepository.save(userRegistration);

         // Kafka
        kafkaTemplate.send("register-sms-events", SmsEvent.builder()
                .toPhone(userRegistration.getPhone())
                .otp(otpVerification.getOtp())
                .build());

        return OtpResponse.builder()
                .phone(request.getPhone())
                .otp(otpVerification.getOtp())
                .build();
    }


    @Override
    public VerifyOtpResponse verifyRegistrationOtp(VerifyOtpRequest request) {
        log.info("Verifying OTP for phone: {}", request.getPhone());

        OtpVerification otpVerification = otpService.getOtp(request.getPhone(), OtpType.REGISTER, request.getOtp());

        UserRegistration userRegistration = getUserRegistration(request.getPhone());

        updateStatusRegistration(userRegistration, OTP_VERIFIED);

        // delete OTP
        otpVerificationRepository.delete(otpVerification);

        return VerifyOtpResponse.builder()
                .phone(request.getPhone())
                .verified(true)
                .build();
    }


    @Override
    public UserDetailsResponse addUserDetails(RegisterDetailsRequest request) {
        log.info("Adding user details for phone: {}", request.getPhone());

        UserRegistration userRegistration = getUserRegistration(request.getPhone());

        userRegistration.setName(request.getName());
        userRegistration.setEmail(request.getEmail());

        updateStatusRegistration(userRegistration, DETAILS_ADDED);

        return UserDetailsResponse.builder()
                .phone(request.getPhone())
                .name(request.getName())
                .email(request.getEmail())
                .status(userRegistration.getStatus())
                .build();
    }


    @Override
    public TokenResponse createUserAndSetPassword(SetPasswordRequest request) throws JOSEException {
        log.info("Creating user and setting password for phone: {}", request.getPhone());

        if(!request.getNewPassword().equals(request.getConfirmPassword())){
            throw new InvalidDataException("Password and Confirm Password do not match");
        }

        UserRegistration userRegistration = getUserRegistration(request.getPhone());

        updateStatusRegistration(userRegistration, COMPLETED);

        User user = User.builder()
                .phone(userRegistration.getPhone())
                .email(userRegistration.getEmail())
                .password(passwordEncoder.encode(request.getNewPassword()))
                .status(EntityStatus.ACTIVE)
                .build();

        userRepository.save(user);
        userHasRoleService.saveUserHasRole(user, RoleEnum.USER);

        // create profile
        profileClient.createProfile(UserProfileRequest.builder()
                        .userId(user.getId())
                        .name(user.getName())
                        .phone(user.getPhone())
                        .email(user.getEmail())
                .build());

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

    @Override
    public void changePassword(ChangePasswordRequest request) {
        log.info("Changing password for current user");

        User user = getUserByEmail(getCurrentUsername());

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new InvalidDataException("Password and Confirm Password do not match");
        }

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new InvalidDataException("Old password is incorrect");
        }

        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            throw new InvalidDataException("New password must be different from old password");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    @Override
    public void logout(TokenRequest request) throws ParseException, JOSEException {
        log.info("Logging out");

        SignedJWT signToken = tokenService.verifyToken(request.getAccessToken(), TokenType.ACCESS_TOKEN);

        String email = signToken.getJWTClaimsSet().getJWTID();
        Date expiryTime = signToken.getJWTClaimsSet().getExpirationTime();

        RevokedToken revokedToken = RevokedToken.builder()
                .accessToken(request.getAccessToken())
                .email(email)
                .expiryTime(expiryTime)
                .ttl((expiryTime.getTime() - System.currentTimeMillis()) / 1000)
                .build();

        revokedTokenService.saveRevokedToken(revokedToken);
    }

    @Override
    public IntrospectResponse introspect(TokenRequest request) {
        try {
            SignedJWT signedJWT = tokenService.verifyToken(request.getAccessToken(), TokenType.ACCESS_TOKEN);

            // lấy userId từ subject
            Long userId = Long.valueOf(signedJWT.getJWTClaimsSet().getSubject());
            List<String> roles = (List<String>) signedJWT.getJWTClaimsSet().getClaim("roles");

            return IntrospectResponse.builder()
                    .valid(true)
                    .userId(userId)
                    .roles(roles)
                    .build();

        } catch (Exception e) {
            return IntrospectResponse.builder()
                    .valid(false)
                    .userId(null)
                    .roles(null)
                    .build();
        }
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

    // info tu access token
    public String getCurrentUsername(){
        var context = SecurityContextHolder.getContext();
        var authentication = context.getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            throw new ForBiddenException("Anonymous user is not allowed");
        }
        return authentication.getName(); // email
    }

}
