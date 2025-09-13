package com.lean2708.auth_service.service.impl;
import com.lean2708.auth_service.entity.RevokedToken;
import com.lean2708.auth_service.repository.RevokedTokenRepository;
import com.lean2708.auth_service.service.RevokedTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Slf4j(topic = "REVOKED-TOKEN-SERVICE")
@RequiredArgsConstructor
@Service
public class RevokedTokenServiceImpl implements RevokedTokenService {

    private final RevokedTokenRepository revokedTokenRepository;


    @Override
    public void saveRevokedToken(RevokedToken revokedToken) {
        revokedTokenRepository.save(revokedToken);
    }

}
