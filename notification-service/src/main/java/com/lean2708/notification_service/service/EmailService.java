package com.lean2708.notification_service.service;

import com.lean2708.notification_service.dto.event.EmailEvent;

import java.io.IOException;

public interface EmailService {

    void sendOtpRegisterEmail(EmailEvent event) throws IOException;

    void sendPasswordResetCode(EmailEvent event) throws IOException;
}
