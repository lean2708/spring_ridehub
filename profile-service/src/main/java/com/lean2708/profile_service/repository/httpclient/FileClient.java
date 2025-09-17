package com.lean2708.profile_service.repository.httpclient;

import com.lean2708.common_library.dto.response.ApiResponse;
import com.lean2708.profile_service.dto.response.FileResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@FeignClient(name = "file-service", path = "/files")
public interface FileClient {

    @PostMapping(value = "/internal/upload/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ApiResponse<List<FileResponse>> uploadImage(@RequestPart("files") List<MultipartFile> files);
}
