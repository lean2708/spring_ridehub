package com.lean2708.profile_service.config;

import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;



@Slf4j(topic = "GATEWAY-FILTER")
@Component
public class GatewayAuthFilter extends OncePerRequestFilter {

    @Value("${gateway.allowed-window-ms:60000}")
    private long allowedWindowMs;

    @Value("${gateway.jwt.secret}")
    private String gatewayJwtSecret;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();

        // skip internal
        if (path.startsWith("/internal/") || path.contains("/internal/")) {
            log.info("Skipping auth for internal API: {}", path);
            filterChain.doFilter(request, response);
            return;
        }

        String skipAuth = request.getHeader("X-Skip-Auth");

        // skip WHITELIST
        if (skipAuth != null && skipAuth.equals("true")) {
            log.info("Skipping auth for {}", path);
            filterChain.doFilter(request, response);
            return;
        }

        String userId = request.getHeader("X-User-Id");
        String tsHeader = request.getHeader("X-Gateway-Ts");
        String rolesHeader = request.getHeader("X-Roles");
        String gatewayJwt = request.getHeader("X-Gateway-Jwt");

        if (userId == null || tsHeader == null || gatewayJwt == null) {
            log.warn("Missing gateway headers for request {}", path);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing gateway headers");
            return;
        }

        try {
            long tsLong = Long.parseLong(tsHeader);
            if (Math.abs(System.currentTimeMillis() - tsLong) > allowedWindowMs) {
                log.warn("Stale request {} (timestamp {})", path, tsHeader);
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Stale request");
                return;
            }
        } catch (NumberFormatException e) {
            log.warn("Invalid timestamp {} for request {}", tsHeader, path);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid timestamp");
            return;
        }

        if (!verifyGatewayJwt(gatewayJwt)) {
            log.warn("Invalid Gateway JWT for request {}", path);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid Gateway JWT");
            return;
        }

        log.info("Authenticated request {} for userId {}", path, userId);

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userId, null);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }

    private boolean verifyGatewayJwt(String token) {
        try {
            // Parse & verify chữ ký
            JWSObject jws = JWSObject.parse(token);
            if (!jws.verify(new MACVerifier(gatewayJwtSecret.getBytes()))) {
                return false;
            }

            // Parse claims
            JWTClaimsSet claims = JWTClaimsSet.parse(jws.getPayload().toJSONObject());

            // Check expiration
            Date expiry = claims.getExpirationTime();
            return expiry != null && expiry.after(new Date());

        } catch (Exception e) {
            return false;
        }
    }


    private List<SimpleGrantedAuthority> parseRoles(String rolesHeader) {
        if (rolesHeader == null || rolesHeader.isBlank()) {
            return Collections.emptyList();
        }
        return Arrays.stream(rolesHeader.split(","))
                .filter(role -> !role.isBlank())
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.trim()))
                .toList();
    }
}
