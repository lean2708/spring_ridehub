package com.lean2708.auth_service.service;

import com.lean2708.auth_service.constants.TokenType;
import com.lean2708.auth_service.entity.User;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.KeyLengthException;
import com.nimbusds.jwt.SignedJWT;

import java.text.ParseException;

public interface TokenService {

    String generateToken(User user, TokenType type) throws JOSEException;

    SignedJWT verifyToken(String token, TokenType type) throws JOSEException, ParseException;

    void saveRefreshToken(String token);

}
