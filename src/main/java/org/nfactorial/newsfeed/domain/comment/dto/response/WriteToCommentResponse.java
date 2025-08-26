package org.nfactorial.newsfeed.domain.comment.dto.response;

import java.time.LocalDateTime;

import org.nfactorial.newsfeed.domain.comment.dto.result.WriteToCommentResult;

import lombok.Builder;

@Builder
public record WriteToCommentResponse(
	long id,
	String content,
	LocalDateTime createdAt) {
	public static WriteToCommentResponse of(WriteToCommentResult result) {
		return new WriteToCommentResponse(result.id(), result.content(), result.createdAt());
	}
}
