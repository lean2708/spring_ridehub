package com.lean2708.auth_service.config;

import com.lean2708.auth_service.constants.CorsProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CorsProperties corsProperties;

    private final String[] PUBLIC_URLS  = {
            "/v1/public/**", "/swagger-ui*/**"
    };

    private final String[] ADMIN_URLS = {
            "/v1/admin/**"
    };


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)

                .authorizeHttpRequests(request -> request
                        .requestMatchers(PUBLIC_URLS).permitAll()
//                        .requestMatchers(ADMIN_URLS).hasRole("ADMIN")
                        .anyRequest().permitAll()
                );
//                .oauth2ResourceServer(oauth2 ->
//                        oauth2.jwt(
//                                        jwtConfigurer -> jwtConfigurer
//                                                .decoder(customJwtDecoder)
//                                                .jwtAuthenticationConverter(customJwtAuthenticationConverter)
//                                )
//                                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
//                );
        return http.build();
    }

    @Bean
    public CorsFilter corsFilter(){
        CorsConfiguration corsConfiguration = new CorsConfiguration();

        // cho phép yêu cầu từ cac cong
        corsConfiguration.setAllowedOrigins(corsProperties.getAllowedOrigins());

        corsConfiguration.addAllowedMethod("*"); // cho phép tất cả method
        corsConfiguration.addAllowedHeader("*"); // cho phép tất cả header
        corsConfiguration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
        urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", corsConfiguration); // dang ki cau hinh

        return new CorsFilter(urlBasedCorsConfigurationSource);
    }


    // thiet lap url tren giao dien browser
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer(){
        return web ->
                web.ignoring().requestMatchers("/actuator/**","/v3/**", "webjar/**",
                        "/swagger-ui*/*swagger-initializer.js","/swagger-ui*/**");
    }


    @Bean
    public PasswordEncoder passwordEncoder(){
        return  new BCryptPasswordEncoder(10);
    }
}
