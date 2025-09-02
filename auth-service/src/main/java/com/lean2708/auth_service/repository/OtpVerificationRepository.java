package com.lean2708.auth_service.repository;
import com.lean2708.auth_service.entity.OtpVerification;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface OtpVerificationRepository extends CrudRepository<OtpVerification, String> {
}
