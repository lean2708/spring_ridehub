package com.lean2708.auth_service.controller;
import com.lean2708.auth_service.dto.request.TokenRequest;
import com.lean2708.auth_service.dto.response.IntrospectResponse;
import com.lean2708.auth_service.service.AuthService;
import com.lean2708.common_library.dto.response.ApiResponse;
import com.nimbusds.jose.JOSEException;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;


@Slf4j(topic = "INTERNAL-AUTH-CONTROLLER")
@RequiredArgsConstructor
@Validated
@RestController
@RequestMapping("/internal")
public class InternalAuthController {

    private final AuthService authService;

    @Operation(summary = "Introspect token")
    @PostMapping("/introspect")
    public ApiResponse<IntrospectResponse> introspect(@Valid @RequestBody TokenRequest request) throws JOSEException, ParseException {
        log.info("Received introspect request for token");

        return ApiResponse.<IntrospectResponse>builder()
                .code(HttpStatus.OK.value())
                .result(authService.introspect(request))
                .message("Introspect successful")
                .build();
    }
}
