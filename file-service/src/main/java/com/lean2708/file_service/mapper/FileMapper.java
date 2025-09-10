package com.lean2708.file_service.mapper;

import com.lean2708.file_service.dto.response.FileResponse;
import com.lean2708.file_service.entity.FileEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FileMapper {

    public FileResponse toFileResponse(FileEntity fileEntity){
        return FileResponse.builder()
                .id(fileEntity.getId())
                .fileName(fileEntity.getFileName())
                .imageUrl(fileEntity.getImageUrl())
                .createdBy(fileEntity.getCreatedBy())
                .createdAt(fileEntity.getCreatedAt())
                .build();
    }

    public List<FileResponse> toFileResponseList(List<FileEntity> fileEntityList){
        return fileEntityList.stream()
                .map(this::toFileResponse)
                .toList();
    }
}
