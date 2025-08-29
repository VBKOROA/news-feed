package org.nfactorial.newsfeed.domain.upload.service;

import java.util.ArrayList;
import java.util.List;

import org.nfactorial.newsfeed.domain.post.entity.Post;
import org.nfactorial.newsfeed.domain.upload.component.FileStore;
import org.nfactorial.newsfeed.domain.upload.entity.Upload;
import org.nfactorial.newsfeed.domain.upload.repository.UploadRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UploadService {
    private final FileStore fileStore;
    private final UploadRepository uploadRepository;

    public void uploadFiles(List<MultipartFile> multipartFiles, Post post) {
        boolean hasFiles = multipartFiles != null && (multipartFiles.isEmpty() == false);

        if (hasFiles == false) {
            return;
        }

        List<Upload> uploads = new ArrayList<>();

        for (MultipartFile file : multipartFiles) {
            String uri = fileStore.storeFile(file);
            Upload upload = Upload.of(post, uri, file.getOriginalFilename());
            uploads.add(upload);
        }

        uploadRepository.saveAll(uploads);
    }

    public void deleteAllByPost(Post foundPost) {
        uploadRepository.deleteAllByPost(foundPost);
    }
}
