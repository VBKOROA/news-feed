package org.nfactorial.newsfeed.domain.upload.service;

import org.nfactorial.newsfeed.domain.post.entity.Post;

public interface UploadServiceApi {
    void deleteAllByPost(Post foundPost);
}
