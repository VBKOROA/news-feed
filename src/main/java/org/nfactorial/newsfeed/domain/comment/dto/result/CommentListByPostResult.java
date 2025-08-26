package org.nfactorial.newsfeed.domain.comment.dto.result;

import java.util.List;

import org.nfactorial.newsfeed.domain.comment.dto.projection.ViewCommentFromPostProjection;

public record CommentListByPostResult(
	List<SimpleComment> comments) {
	public static CommentListByPostResult of(List<ViewCommentFromPostProjection> comments) {
		List<SimpleComment> commentList = comments.stream()
			.map(c -> new SimpleComment(
				c.getCommentId(),
				c.getProfileId(),
				c.getNickname(),
				c.getContent(),
				c.getHasInnerComments()))
			.toList();
		return new CommentListByPostResult(commentList);
	}

	public record SimpleComment(
		long commentId,
		long profileId,
		String nickname,
		String content,
		boolean hasInnerComments) {
	}
}
