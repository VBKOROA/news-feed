package org.nfactorial.newsfeed.domain.post.service;

import java.util.List;

import org.nfactorial.newsfeed.common.security.AuthProfileDto;
import org.nfactorial.newsfeed.domain.post.dto.request.PostCreateRequest;
import org.nfactorial.newsfeed.domain.post.dto.response.PostCreateResponse;
import org.nfactorial.newsfeed.domain.post.entity.Post;
import org.nfactorial.newsfeed.domain.post.repository.PostRepository;
import org.nfactorial.newsfeed.domain.profile.entity.Profile;
import org.nfactorial.newsfeed.domain.profile.service.ProfileServiceApi;
import org.nfactorial.newsfeed.domain.upload.service.UploadService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PostCreationService {
    private final ProfileServiceApi profileService;
    private final PostRepository postRepository;
    private final UploadService uploadService;

    @Transactional
    public PostCreateResponse save(PostCreateRequest request, List<MultipartFile> multipartFiles,
        AuthProfileDto currentUserProfile) {
        long profileId = currentUserProfile.profileId();
        Profile foundProfile = profileService.getProfileEntityById(profileId);

        Post savedPost = postRepository.save(Post.of(request, foundProfile));

        uploadService.uploadFiles(multipartFiles, savedPost);

        return PostCreateResponse.of(savedPost);
    }
}
