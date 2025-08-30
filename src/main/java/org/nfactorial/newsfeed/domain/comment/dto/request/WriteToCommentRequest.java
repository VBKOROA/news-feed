package org.nfactorial.newsfeed.domain.comment.dto.request;

import jakarta.validation.constraints.NotBlank;

public record WriteToCommentRequest(
    @NotBlank
	String content) {
}
