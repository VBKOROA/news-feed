package org.nfactorial.newsfeed.domain.comment.service;

import java.util.List;

import org.nfactorial.newsfeed.common.code.ErrorCode;
import org.nfactorial.newsfeed.common.exception.BusinessException;
import org.nfactorial.newsfeed.domain.comment.dto.result.GetCommentsFromCommentResult;
import org.nfactorial.newsfeed.domain.comment.entity.Comment;
import org.nfactorial.newsfeed.domain.comment.repository.CommentRepository;
import org.nfactorial.newsfeed.domain.post.entity.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentService implements CommentServiceApi {
	private final CommentRepository commentRepository;

	@Override
	public int getCommentCount(Post post) {
		List<Comment> comments = commentRepository.findAllByPost(post);
		return comments.size();
	}

	@Transactional
	public void deleteById(long commentId, long profileId) {
		Comment comment = getOwnedComment(commentId, profileId);
		commentRepository.delete(comment);
	}

	@Transactional
	public String updateById(long commentId, String content, long profileId) {
		Comment comment = getOwnedComment(commentId, profileId);
		comment.updateContent(content);
		return comment.getContent();
	}

	@Transactional(readOnly = true)
	public GetCommentsFromCommentResult getCommentsFromComment(long commentId, Pageable pageable) {
		Comment parentComment = commentRepository.findById(commentId)
			.orElseThrow(() -> new BusinessException(ErrorCode.COMMENT_NOT_FOUND));
		Slice<Comment> comments = commentRepository.findAllByParentComment(parentComment, pageable);
		return GetCommentsFromCommentResult.of(comments);
	}

	private Comment getOwnedComment(long commentId, long profileId) {
		Comment comment = commentRepository.findById(commentId)
			.orElseThrow(() -> new BusinessException(ErrorCode.COMMENT_NOT_FOUND));
		if (comment.getProfile().getId() != profileId) {
			throw new BusinessException(ErrorCode.COMMENT_NOT_YOURS);
		}
		return comment;
	}
}
