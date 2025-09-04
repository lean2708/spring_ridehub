package com.lean2708.auth_service.constants;
import com.lean2708.auth_service.exception.InvalidDataException;


public enum RegistrationStatus {

    PENDING,
    OTP_VERIFIED,
    DETAILS_ADDED,
    COMPLETED;


    public boolean canTransitionTo(RegistrationStatus target) {
        return switch (this) {
            case PENDING -> target == OTP_VERIFIED;
            case OTP_VERIFIED -> target == DETAILS_ADDED;
            case DETAILS_ADDED -> target == COMPLETED;
            default -> false;
        };
    }

    public void validateTransition(RegistrationStatus target) {
        if (!canTransitionTo(target)) {
            throw new InvalidDataException(
                    String.format("Invalid transition: %s cannot go to %s", this, target)
            );
        }
    }
}
