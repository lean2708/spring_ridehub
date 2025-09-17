package com.lean2708.file_service.controller;

import com.lean2708.common_library.dto.response.ApiResponse;
import com.lean2708.file_service.dto.response.FileResponse;
import com.lean2708.file_service.exception.FileException;
import com.lean2708.file_service.service.FileService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Slf4j(topic = "INTERNAL-FILE-CONTROLLER")
@RequiredArgsConstructor
@Validated
@RequestMapping("/internal")
@RestController
public class InternalFileController {

    private final FileService fileService;

    @Operation(description = "API upload hình ảnh (có thể tải nhiều ảnh lên)")
    @PostMapping(value = "/upload/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<List<FileResponse>> uploadImage(@RequestPart("files") List<MultipartFile> files) throws IOException, FileException {
        log.info("Received request to upload {} file(s)", files.size());

        return ApiResponse.<List<FileResponse>>builder()
                .code(HttpStatus.OK.value())
                .message("Upload Files")
                .result(fileService.uploadListFile(files))
                .build();
    }
}
