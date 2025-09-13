package com.lean2708.api_gateway.httpclient;

import com.lean2708.api_gateway.dto.request.TokenRequest;
import com.lean2708.api_gateway.dto.response.IntrospectResponse;
import com.lean2708.common_library.dto.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class AuthFeignClient {

    private final WebClient webClient;

    public Mono<IntrospectResponse> introspectToken(String token) {
        TokenRequest request = TokenRequest.builder()
                .accessToken(token)
                .build();

        return webClient.post()
                .uri("/auth/introspect")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ApiResponse<IntrospectResponse>>() {})
                .map(ApiResponse::getResult)
                .onErrorResume(e -> Mono.just(IntrospectResponse.builder().valid(false).userId(null).build()));
    }

}
