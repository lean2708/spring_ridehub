package com.lean2708.notification_service.event;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SmsEvent {

    private String toPhone;
    private String otp;

}
