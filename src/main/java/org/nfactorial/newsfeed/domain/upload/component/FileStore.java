package org.nfactorial.newsfeed.domain.upload.component;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.nfactorial.newsfeed.common.code.ErrorCode;
import org.nfactorial.newsfeed.common.exception.BusinessException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j(topic = "FileStore")
public class FileStore {
    @Value("${file.upload-dir}")
    private String uploadDir;

    public String storeFile(MultipartFile multipartFile) {
        String originalFileName = multipartFile.getOriginalFilename();
        String storedName = internalNameGen(originalFileName);
        String uploadPath = uploadDir + storedName;
        try {
            multipartFile.transferTo(new File(uploadPath));
        } catch (Exception e) {
            log.error("Error occurred: {}", e.getMessage());
            throw new BusinessException(ErrorCode.FILE_SAVE_FAILED);
        }

        return uploadPath;
    }

    public Resource loadFileAsResource(String fullPath) {
        try {
            Path filePath = Paths.get(fullPath);
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                return resource;
            }

            throw new BusinessException(ErrorCode.FILE_NOT_FOUND);
        } catch (Exception e) {
            log.error("Error occurred: {}", e);
            throw new BusinessException(ErrorCode.FILE_NOT_FOUND);
        }
    }

    private String internalNameGen(String fileName) {
        String ext = extractExt(fileName);
        String uuid = UUID.randomUUID().toString();
        return uuid + ext;
    }

    public String extractExt(String fileName) {
        int pos = fileName.lastIndexOf(".");
        return fileName.substring(pos);
    }
}
