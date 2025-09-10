package com.lean2708.file_service.service;

import com.lean2708.common_library.dto.response.PageResponse;
import com.lean2708.file_service.dto.response.FileResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface FileService {

    List<FileResponse> uploadListFile(List<MultipartFile> files) throws IOException;

    PageResponse<FileResponse> getAllFiles(Pageable pageable);

    void deleteFile(Long id) throws IOException;
}
