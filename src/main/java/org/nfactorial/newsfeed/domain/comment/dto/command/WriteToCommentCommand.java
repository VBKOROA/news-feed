package org.nfactorial.newsfeed.domain.comment.dto.command;

import lombok.Builder;

@Builder
public record WriteToCommentCommand(
	long profileId,
	long commentId,
	String content) {

	public static WriteToCommentCommand of(long commentId, long profileId, String content) {
		return WriteToCommentCommand.builder()
			.commentId(commentId)
			.profileId(profileId)
			.content(content)
			.build();
	}
}
