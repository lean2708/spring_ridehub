package com.lean2708.file_service.controller;

import com.lean2708.common_library.dto.response.ApiResponse;
import com.lean2708.common_library.dto.response.PageResponse;
import com.lean2708.file_service.dto.response.FileResponse;
import com.lean2708.file_service.exception.FileException;
import com.lean2708.file_service.service.FileService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;


@Slf4j(topic = "FILE-CONTROLLER")
@RequiredArgsConstructor
@Validated
@RequestMapping("/files")
@RestController
public class FileController {

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


    @GetMapping("/all")
    public ApiResponse<PageResponse<FileResponse>> getAllFiles(@ParameterObject @PageableDefault Pageable pageable){
        log.info("Received request to fetch all files with pagination: page={}, size={}",
                pageable.getPageNumber(), pageable.getPageSize());

        return ApiResponse.<PageResponse<FileResponse>>builder()
                .code(HttpStatus.OK.value())
                .result(fileService.getAllFiles(pageable))
                .message("Fetch All Files With Pagination")
                .build();
    }


    @DeleteMapping("/delete/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) throws Exception {
        log.info("Received request to delete file with id={}", id);

        fileService.deleteFile(id);
        return ApiResponse.<Void>builder()
                .code(HttpStatus.OK.value())
                .message("Delete File")
                .result(null)
                .build();
    }

}

