package org.nfactorial.newsfeed.domain.post.dto.projection;

import java.time.LocalDateTime;
import java.util.List;

public record PostViewProjection(
    Long postId,
    Long profileId,
    String nickname,
    String content,
    int likeCount,
    long commentCount,
    int viewCount,
    boolean hasLikedPost,
    LocalDateTime createdAt,
    LocalDateTime modifiedAt,
    List<SimpleFileProjection> files,
    List<SimpleCommentProjection> comments) {
    public record SimpleFileProjection(
        long fileId,
        String fileName) {
    }

    public record SimpleCommentProjection(
        long commentId,
        long profileId,
        String nickname,
        String contents,
        boolean hasInnerComments) {
    }
}