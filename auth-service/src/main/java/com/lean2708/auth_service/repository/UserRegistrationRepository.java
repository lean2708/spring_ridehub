package com.lean2708.auth_service.repository;
import com.lean2708.auth_service.entity.UserRegistration;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface UserRegistrationRepository extends CrudRepository<UserRegistration, String> {

}
