package org.nfactorial.newsfeed.domain.comment.dto.result;

import java.util.List;

import org.nfactorial.newsfeed.domain.comment.entity.Comment;
import org.springframework.data.domain.Slice;

public record GetCommentsFromCommentResult(
    int currentPage,
    boolean hasNext,
    List<SimpleComment> comments) {
    public record SimpleComment(
        long commentId,
        long profileId,
        String nickname,
        String content) {
    }

    public static GetCommentsFromCommentResult of(Slice<Comment> commentSlice) {
        List<SimpleComment> commentList = commentSlice.getContent().stream()
            .map(c -> new SimpleComment(
                c.getId(),
                c.getProfile().getId(),
                c.getProfile().getNickname(),
                c.getContent()))
            .toList();
        return new GetCommentsFromCommentResult(
            commentSlice.getNumber(),
            commentSlice.hasNext(),
            commentList);
    }
}
