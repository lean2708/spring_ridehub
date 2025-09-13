package com.lean2708.notification_service.service.impl;

import com.lean2708.notification_service.event.SmsEvent;
import com.lean2708.notification_service.httpclient.SmsClient;
import com.lean2708.notification_service.service.SmsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Slf4j(topic = "SMS-SERVICE")
@Service
@RequiredArgsConstructor
public class SmsServiceImpl implements SmsService {

    private final SmsClient smsClient;


    @Value("${sms.device.id}")
    private String deviceId;

    @Value("${sms.api.username}")
    private String username;

    @Value("${sms.api.password}")
    private String password;



    @Override
    public void handleRegisterSmsEvent(SmsEvent smsEvent) throws Exception {
        log.info("Handling Register Sms Event: toPhone={}", smsEvent.getToPhone());

        sendSms(smsEvent.getToPhone(), "Xin chào bạn. Mã OTP đăng ký của bạn là: " + smsEvent.getOtp());
    }


    @Override
    public void handleResetPasswordSmsEvent(SmsEvent smsEvent) throws Exception {
        log.info("Handling Reset Password Sms Event: toPhone={}", smsEvent.getToPhone());

        sendSms(smsEvent.getToPhone(), "Xin chào bạn. Mã OTP đặt lại mật khẩu của bạn là: " + smsEvent.getOtp());
    }


    public void sendSms(String toPhone, String messageText) throws Exception {
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
        body.put("phoneNumbers", Collections.singletonList(toPhone));
        body.put("simNumber", 1);
        body.put("ttl", 600);

        try {
            log.info("Request Body: {}", body);

            String response = smsClient.sendMessage(encodedAuth, body);

            log.info("Response from SMS API: {}", response);
        } catch (Exception e) {
            log.error("Error when calling SMS API: {}", e.getMessage(), e);
            throw new Exception("Failed to send SMS to " + toPhone, e);
        }

    }




}
