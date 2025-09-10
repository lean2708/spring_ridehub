package com.lean2708.auth_service.config;


import com.lean2708.auth_service.constants.TokenType;
import com.lean2708.auth_service.exception.UnauthenticatedException;
import com.lean2708.auth_service.service.TokenService;
import com.nimbusds.jose.JOSEException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Component;

import java.text.ParseException;

@RequiredArgsConstructor
@Component
public class CustomJwtDecoder implements JwtDecoder {

    private final TokenService tokenService;
    // lay tu JwtDecoderConfig
    private final NimbusJwtDecoder nimbusJwtDecoder;

    @Override
    public Jwt decode(String token) throws JwtException {
        try {
            // check token (signer key, blacklist, ...)
            tokenService.verifyToken(token, TokenType.ACCESS_TOKEN);
        } catch (JOSEException | ParseException e) {
            throw new BadJwtException(e.getMessage());
        } catch (UnauthenticatedException ex){
            throw new BadJwtException("Token không hợp lệ");
        }
        return nimbusJwtDecoder.decode(token);
    }
}
