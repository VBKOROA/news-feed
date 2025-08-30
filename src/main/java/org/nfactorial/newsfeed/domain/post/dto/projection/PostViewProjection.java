package org.nfactorial.newsfeed.domain.post.dto.projection;

import java.time.LocalDateTime;
import java.util.List;

public record PostViewProjection(
    Long postId,
    Long profileId,
    String nickname,
    String content,
    Integer likeCount,
    Long commentCount,
    Integer viewCount,
    Boolean hasLikedPost,
    LocalDateTime createdAt,
    LocalDateTime modifiedAt,
    List<SimpleFileProjection> files,
    List<SimpleCommentProjection> comments) {
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