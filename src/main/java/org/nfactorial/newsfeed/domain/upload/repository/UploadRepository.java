package org.nfactorial.newsfeed.domain.upload.repository;

import org.nfactorial.newsfeed.domain.post.entity.Post;
import org.nfactorial.newsfeed.domain.upload.entity.Upload;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UploadRepository extends JpaRepository<Upload, Long> {
    void deleteAllByPost(Post foundPost);
}
