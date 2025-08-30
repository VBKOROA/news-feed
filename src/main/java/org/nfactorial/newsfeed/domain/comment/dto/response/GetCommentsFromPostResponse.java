package org.nfactorial.newsfeed.domain.comment.dto.response;

import java.util.List;

import org.nfactorial.newsfeed.domain.comment.dto.result.CommentListByPostResult;

public record GetCommentsFromPostResponse(
	List<SimpleComment> comments) {
	public static GetCommentsFromPostResponse of(CommentListByPostResult comments) {
		List<SimpleComment> commentList = comments.comments().stream()
			.map(SimpleComment::of)
			.toList();
		return new GetCommentsFromPostResponse(commentList);
	}

	public record SimpleComment(
		long commentId,
		long profileId,
		String nickname,
		String content,
		boolean hasInnerComments) {
		public static SimpleComment of(CommentListByPostResult.SimpleComment comment) {
			return new SimpleComment(
				comment.commentId(),
				comment.profileId(),
				comment.nickname(),
				comment.content(),
				comment.hasInnerComments());
		}
	}
}
