package org.nfactorial.newsfeed.domain.upload.component;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.nfactorial.newsfeed.common.code.ErrorCode;
import org.nfactorial.newsfeed.common.exception.BusinessException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
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
            throw new BusinessException(ErrorCode.FILE_SAVE_FAILED);
        }

        return uploadPath;
    }

    private String internalNameGen(String fileName) {
        String ext = extractExt(fileName);
        String uuid = UUID.randomUUID().toString();
        return uuid + ext;
    }

    private String extractExt(String fileName) {
        int pos = fileName.lastIndexOf(".");
        return fileName.substring(pos);
    }
}
