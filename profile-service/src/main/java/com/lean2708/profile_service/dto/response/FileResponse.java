package com.lean2708.profile_service.dto.response;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FileResponse {

    private Long id;
    private String fileName;
    private String imageUrl;

    private String createdBy;
    private LocalDate createdAt;
}
