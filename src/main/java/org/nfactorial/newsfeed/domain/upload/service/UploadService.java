package org.nfactorial.newsfeed.domain.upload.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.nfactorial.newsfeed.common.code.ErrorCode;
import org.nfactorial.newsfeed.common.exception.BusinessException;
import org.nfactorial.newsfeed.domain.post.entity.Post;
import org.nfactorial.newsfeed.domain.upload.component.FileStore;
import org.nfactorial.newsfeed.domain.upload.dto.PrepareDownloadResult;
import org.nfactorial.newsfeed.domain.upload.entity.Upload;
import org.nfactorial.newsfeed.domain.upload.repository.UploadRepository;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UploadService implements UploadServiceApi {
    private final FileStore fileStore;
    private final UploadRepository uploadRepository;

    private static final Set<String> EXT_WHITELIST = Set.of(".jpg", ".png", ".webp", ".pdf", ".docx");
    private static final long MAXIMUM_FILE_SIZE_BYTE = 1000 * 1000 * 10L;

    public void uploadFiles(List<MultipartFile> multipartFiles, Post post) {
        boolean hasFiles = multipartFiles != null && (multipartFiles.isEmpty() == false);

        if (hasFiles == false) {
            return;
        }

        List<Upload> uploads = new ArrayList<>();

        for (MultipartFile file : multipartFiles) {
            validateFile(file);
            String uri = fileStore.storeFile(file);
            Upload upload = Upload.of(post, uri, file.getOriginalFilename());
            uploads.add(upload);
        }

        uploadRepository.saveAll(uploads);
    }

    @Override
    public void deleteAllByPost(Post foundPost) {
        uploadRepository.deleteAllByPost(foundPost);
    }

    @Transactional(readOnly = true)
    public PrepareDownloadResult prepareDownload(long uploadId) {
        Upload upload = uploadRepository.findById(uploadId)
            .orElseThrow(() -> new BusinessException(ErrorCode.UPLOAD_NOT_FOUND));

        String path = upload.getUri();
        Resource resource = fileStore.loadFileAsResource(path);

        return new PrepareDownloadResult(resource, resource.getFilename());
    }

    private void validateFile(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        String ext = fileStore.extractExt(fileName)
            .orElseThrow(() -> new BusinessException(ErrorCode.EXT_NOT_ALLOWED));

        if (EXT_WHITELIST.contains(ext) == false) {
            throw new BusinessException(ErrorCode.EXT_NOT_ALLOWED);
        }

        if (file.getSize() > MAXIMUM_FILE_SIZE_BYTE) {
            throw new BusinessException(ErrorCode.FILE_SIZE_TOO_BIG);
        }
    }
}
