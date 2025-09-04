package com.lean2708.auth_service.repository;
import com.lean2708.auth_service.entity.RevokedToken;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface RevokedTokenRepository extends CrudRepository<RevokedToken, String> {

    boolean existsById(String accessToken);
}
