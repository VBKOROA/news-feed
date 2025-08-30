package org.nfactorial.newsfeed.domain.upload.controller;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import javax.print.attribute.standard.Media;

import org.nfactorial.newsfeed.domain.upload.dto.PrepareDownloadResult;
import org.nfactorial.newsfeed.domain.upload.service.UploadService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api/v1/uploads")
@RequiredArgsConstructor
public class UploadController {
    private final UploadService uploadService;

    private static final String DEFAULT_CONTENT_TYPE = "application/octet-stream";

    @GetMapping("/{uploadId}")
    public ResponseEntity<Resource> downloadFile(@PathVariable("uploadId")
    long uploadId) {
        PrepareDownloadResult result = uploadService.prepareDownload(uploadId);
        Resource resource = result.resource();
        String fileName = result.fileName();
        String contentDisposition = "attachment; filename=\"" +
            URLEncoder.encode(fileName, StandardCharsets.UTF_8) + "\"";

        String contentType = DEFAULT_CONTENT_TYPE;

        try {
            contentType = Files.probeContentType(resource.getFile().toPath());
        } catch (Exception e) {
        }

        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(contentType))
            .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
            .body(resource);
    }
}
