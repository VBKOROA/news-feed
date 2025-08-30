package org.nfactorial.newsfeed.domain.post.dto.projection;

import java.time.LocalDateTime;

public interface PostViewProjection {
    public record PostDetailProjection(
        Long postId,
        Long profileId,
        String nickname,
        String content,
        Integer likeCount,
        Long commentCount,
        Integer viewCount,
        Boolean hasLikedPost,
        LocalDateTime createdAt,
        LocalDateTime modifiedAt) {
    }

    public record SimpleFileProjection(
        Long fileId,
        String fileName) {
    }

    public record SimpleCommentProjection(
        Long commentId,
        Long profileId,
        String nickname,
        String contents,
        Boolean hasInnerComments) {
    }
}