package com.lean2708.notification_service.controller;

import com.lean2708.notification_service.event.SmsEvent;
import com.lean2708.notification_service.service.SmsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.web.bind.annotation.RestController;

@Slf4j(topic = "SMS-CONTROLLER")
@RestController
@RequiredArgsConstructor
public class SmsController {

    private final SmsService smsService;

    @KafkaListener(topics = "register-sms-events", groupId = "notification-service")
    public void sendSmsRegister(SmsEvent smsEvent) throws Exception {
        log.info("Received register-sms-event: phone={}", smsEvent.getToPhone());

        smsService.handleRegisterSmsEvent(smsEvent);
    }

    @KafkaListener(topics = "reset-password-sms-events", groupId = "notification-service")
    public void sendSmsResetPassword(SmsEvent smsEvent) throws Exception {
        log.info("Received reset-password-sms-event: phone={}", smsEvent.getToPhone());

        smsService.handleResetPasswordSmsEvent(smsEvent);
    }
}

