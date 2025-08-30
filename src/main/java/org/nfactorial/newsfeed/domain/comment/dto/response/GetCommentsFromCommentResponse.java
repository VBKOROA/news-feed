package org.nfactorial.newsfeed.domain.comment.dto.response;

import java.util.List;

import org.nfactorial.newsfeed.domain.comment.dto.result.GetCommentsFromCommentResult;

public record GetCommentsFromCommentResponse(
    int currentPage,
    boolean hasNext,
    List<SimpleComment> comments) {
    public record SimpleComment(
        long commentId,
        long profileId,
        String nickname,
        String content) {
    }

    public static GetCommentsFromCommentResponse of(GetCommentsFromCommentResult commentResult) {
        List<SimpleComment> comments = commentResult.comments().stream()
            .map(c -> new SimpleComment(
                c.commentId(),
                c.profileId(),
                c.nickname(),
                c.content()))
            .toList();
        return new GetCommentsFromCommentResponse(
            commentResult.currentPage(),
            commentResult.hasNext(),
            comments);
    }
}
