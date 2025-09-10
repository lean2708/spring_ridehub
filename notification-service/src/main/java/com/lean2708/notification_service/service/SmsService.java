package com.lean2708.notification_service.service;

import com.lean2708.notification_service.event.SmsEvent;
import com.lean2708.notification_service.httpclient.SmsClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Slf4j(topic = "SMS-SERVICE")
@Service
@RequiredArgsConstructor
public class SmsService {

    private final SmsClient smsClient;


    @Value("${sms.device.id}")
    private String deviceId;

    @Value("${sms.api.username}")
    private String username;

    @Value("${sms.api.password}")
    private String password;


    @KafkaListener(topics = "register-sms-events", groupId = "notification-service")
    public void handleRegisterSmsEvent(SmsEvent smsEvent) {
        log.info("Received register-sms-event: phone={}", smsEvent.getToPhone());

        sendSms(smsEvent.getToPhone(), "Xin chào bạn. Mã OTP đăng ký của bạn là: " + smsEvent.getOtp());
    }

    @KafkaListener(topics = "reset-password-sms-events", groupId = "notification-service")
    public void handleResetPasswordSmsEvent(SmsEvent smsEvent) {
        log.info("Received reset-password-sms-event: phone={}", smsEvent.getToPhone());

        sendSms(smsEvent.getToPhone(), "Xin chào bạn. Mã OTP đặt lại mật khẩu của bạn là: " + smsEvent.getOtp());
    }


    public void sendSms(String toPhone, String messageText) {
        log.info("Sending SMS to {}", toPhone);

        String auth = username + ":" + password;
        String encodedAuth = "Basic " + Base64.getEncoder()
                .encodeToString(auth.getBytes(StandardCharsets.UTF_8));

        // Build body
        Map<String, Object> body = new HashMap<>();
        Map<String, Object> textMessage = new HashMap<>();
        textMessage.put("text", messageText);

        body.put("textMessage", textMessage);
        body.put("deviceId", deviceId);
        body.put("phoneNumbers", new String[]{toPhone});
        body.put("simNumber", 1);
        body.put("ttl", 600);

        try {
            String response = smsClient.sendMessage(encodedAuth, body);
            log.info("Response from SMS API: {}", response);
        } catch (Exception e) {
            log.error("Error when calling SMS API: {}", e.getMessage(), e);
        }

    }




}
