package org.nfactorial.newsfeed.domain.post.service;

import java.util.List;
import java.util.Set;

import org.nfactorial.newsfeed.common.code.ErrorCode;
import org.nfactorial.newsfeed.common.exception.BusinessException;
import org.nfactorial.newsfeed.domain.post.dto.projection.PostViewProjection;
import org.nfactorial.newsfeed.domain.post.repository.PostQueryRepository;
import org.nfactorial.newsfeed.domain.post.repository.PostRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PostInteractionService {
    private final PostRepository postRepository;
    private final PostQueryRepository postQueryRepository;

    @Transactional
    public PostViewProjection viewPost(long postId, long viewerProfileId) {
        postRepository.incrementViewCount(postId);

        PostViewProjection result = postQueryRepository.findViewPostProjectionByPostId(postId, viewerProfileId)
            .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));
        var comments = result.comments();
        boolean hasComments = comments != null && (comments.isEmpty() == false);

        if (hasComments == false) {
            return result;
        }

        List<Long> commentIds = comments.stream().map(c -> c.commentId()).toList();
        Set<Long> parentCommentIds = postQueryRepository.findParentCommentsInCommentIds(commentIds);

        var finalComments = comments.stream()
            .map(c -> new PostViewProjection.SimpleCommentProjection(c.commentId(), c.profileId(), c.nickname(),
                c.contents(),
                parentCommentIds.contains(c.commentId())))
            .toList();

        return new PostViewProjection(
            result.postId(),
            result.profileId(),
            result.nickname(),
            result.content(),
            result.likeCount(),
            result.commentCount(),
            result.viewCount(),
            result.hasLikedPost(),
            result.createdAt(),
            result.modifiedAt(),
            result.files(),
            finalComments);
    }
}
