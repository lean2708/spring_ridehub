package com.lean2708.file_service.entity;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.CreatedBy;

import java.time.LocalDate;

@Getter
@Setter
@Table(name = "files")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class FileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String publicId;
    private String fileName;
    private String imageUrl;

    @CreatedBy
    private String createdBy;
    @CreationTimestamp
    private LocalDate createdAt;



}

