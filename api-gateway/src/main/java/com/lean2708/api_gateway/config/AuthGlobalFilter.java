package com.lean2708.api_gateway.config;
import com.lean2708.api_gateway.dto.response.IntrospectResponse;
import com.lean2708.api_gateway.httpclient.AuthFeignClient;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.UUID;


@Slf4j
@Configuration
@RequiredArgsConstructor
public class AuthGlobalFilter implements GlobalFilter, Ordered {

    private final AuthFeignClient authFeignClient;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Value("${gateway.jwt.secret}")
    private String gatewayJwtSecret;

    @Value("${gateway.jwt.expiry-in-minutes}")
    private long gatewayJwtExpiryMinutes;

    @Value("${app.api-prefix}")
    private String apiPrefix;

    private static final List<String> WHITELIST_NO_PREFIX = List.of(
            "**/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html"
    );

    // Có prefix gateway (api public for gateway)
    private static final List<String> WHITELIST_WITH_PREFIX = List.of(
            "**/v3/api-docs",
            "/auth/login"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        log.info("Authentication filter...");

        // check WHITELIST
        if (isPublicEndpoint(exchange.getRequest())) {
            ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                    .header("X-Skip-Auth", "true")
                    .build();

            return chain.filter(exchange.mutate().request(mutatedRequest).build());
        }

        List<String> authHeaders = exchange.getRequest().getHeaders().getOrEmpty(HttpHeaders.AUTHORIZATION);
        if (authHeaders.isEmpty()) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String authHeader = authHeaders.get(0);
        if (!authHeader.startsWith("Bearer ")) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
        String token = authHeader.substring(7);

        Mono<IntrospectResponse> introspectMono = authFeignClient.introspectToken(token);

        return introspectMono.flatMap(result -> {

            // Token khong hop le
            if (result == null || !result.isValid()) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            Long userId = result.getUserId();
            List<String> roles = result.getRoles();

            // generate token gateway
            String gatewayJwt;
            try {
                gatewayJwt = generateGatewayJwt(userId, roles, gatewayJwtExpiryMinutes * 60 * 1000L);
            } catch (JOSEException e) {
                throw new RuntimeException(e);
            }

            // Token hop le
            ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                    .header("X-User-Id", String.valueOf(userId))
                    .header("X-Roles", String.join(",", roles))
                    .header("X-Gateway-Ts", String.valueOf(System.currentTimeMillis()))
                    .header("X-Gateway-Jwt", gatewayJwt)
                    .build();

            return chain.filter(exchange.mutate().request(mutatedRequest).build());
        })
        .onErrorResume(e -> {
            log.error("Introspect call failed", e);
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        });
    }


    @Override
    public int getOrder() {
        return -100;
    }


    private boolean isPublicEndpoint(ServerHttpRequest request) {
        String path = request.getURI().getPath();
        return WHITELIST_NO_PREFIX.stream().anyMatch(s -> pathMatcher.match(s, path)) ||
                WHITELIST_WITH_PREFIX.stream().anyMatch(s -> pathMatcher.match(apiPrefix + s, path)) ;
    }


    public String generateGatewayJwt(Long userId, List<String> roles, long ttlMillis) throws JOSEException {
        // Header
        JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.HS512)
                .type(JOSEObjectType.JWT)
                .build();

        // Payload
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(String.valueOf(userId))
                .issuer("api-gateway")
                .issueTime(Date.from(Instant.now()))
                .expirationTime(Date.from(Instant.now().plusSeconds(ttlMillis)))
                .jwtID(UUID.randomUUID().toString())
                .claim("roles", roles)
                .build();

        // JWS object
        JWSObject jwsObject = new JWSObject(header, new Payload(jwtClaimsSet.toJSONObject()));

        // Ký
        jwsObject.sign(new MACSigner(gatewayJwtSecret.getBytes()));

        return jwsObject.serialize();
    }


}
