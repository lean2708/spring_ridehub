package com.lean2708.auth_service.service;

import com.lean2708.auth_service.entity.RevokedToken;

public interface RevokedTokenService {

    void saveRevokedToken(RevokedToken revokedToken);

}
