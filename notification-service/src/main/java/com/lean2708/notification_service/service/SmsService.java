package com.lean2708.notification_service.service;

import com.lean2708.notification_service.event.SmsEvent;

public interface SmsService {

    void handleRegisterSmsEvent(SmsEvent smsEvent) throws Exception;

    void handleResetPasswordSmsEvent(SmsEvent smsEvent) throws Exception;
}
