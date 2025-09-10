package com.lean2708.notification_service.httpclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.Map;

@FeignClient(name = "smsClient", url = "${sms.api.url}")
public interface SmsClient {

    @PostMapping("?skipPhoneValidation=true&deviceActiveWithin=0")
    String sendMessage(
            @RequestHeader("Authorization") String authorization,
            @RequestBody Map<String, Object> body
    );

}
