package com.lean2708.notification_service.dto.event;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailEvent {

    private String toEmail;
    private String name;
    private String otp;

}
