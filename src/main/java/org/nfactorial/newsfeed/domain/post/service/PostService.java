package org.nfactorial.newsfeed.domain.post.service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.nfactorial.newsfeed.common.code.ErrorCode;
import org.nfactorial.newsfeed.common.exception.BusinessException;
import org.nfactorial.newsfeed.common.security.AuthProfileDto;
import org.nfactorial.newsfeed.domain.post.dto.PostCountDto;
import org.nfactorial.newsfeed.domain.post.dto.projection.PostViewProjection;
import org.nfactorial.newsfeed.domain.post.dto.request.PostUpdateRequest;
import org.nfactorial.newsfeed.domain.post.dto.response.PostUpdateResponse;
import org.nfactorial.newsfeed.domain.post.entity.Post;
import org.nfactorial.newsfeed.domain.post.repository.PostQueryRepository;
import org.nfactorial.newsfeed.domain.post.repository.PostRepository;
import org.nfactorial.newsfeed.domain.upload.service.UploadService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PostService implements PostServiceApi {

	private final PostRepository postRepository;

	private final GetPostByIdHelper getPostByIdHelper;

	private final UploadService uploadService;

	private final PostQueryRepository postQueryRepository;

	@Transactional
	public PostUpdateResponse update(Long postId, PostUpdateRequest request,
		AuthProfileDto currentUserProfile) {

		Post foundPost = getPostById(postId);

		if (!ObjectUtils.nullSafeEquals(foundPost.getProfile().getId(), currentUserProfile.profileId())) {
			throw new BusinessException(ErrorCode.POST_ACCESS_DENIED);
		}

		foundPost.updateContent(request.content());

		return PostUpdateResponse.of(foundPost);
	}

	@Transactional
	public void deleteById(Long postId, AuthProfileDto currentUserProfile) {

		Post foundPost = getPostById(postId);

		if (!ObjectUtils.nullSafeEquals(foundPost.getProfile().getId(), currentUserProfile.profileId())) {
			throw new BusinessException(ErrorCode.POST_ACCESS_DENIED);
		}

		uploadService.deleteAllByPost(foundPost);

		postRepository.delete(foundPost);
	}

	// 포스트 찾기
	@Override
	@Transactional(readOnly = true)
	public Post getPostById(long postId) {
		return getPostByIdHelper.execute(postId);
	}

	@Override
	public long countPostsByProfileId(long profileId) {
		return postRepository.countByProfileId(profileId);
	}

	@Override
	public Map<Long, Long> countPostsByProfileIds(List<Long> profileIds) {
		return postRepository.countPostsByProfileIds(profileIds).stream()
			.collect(Collectors.toMap(
				PostCountDto::profileId,
				PostCountDto::postCount));
	}

	@Transactional
	public Post getPostByIdWithLock(Long postId) {
		return postRepository.findByIdWithPessimisticLock(postId)
			.orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));
	}

	@Transactional(readOnly = true)
	public PostViewProjection viewPost(long postId, long viewerProfileId) {
		postRepository.incrementViewCount(postId);

		PostViewProjection result = postQueryRepository.findViewPostProjectionByPostId(postId, viewerProfileId)
			.orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));
		var comments = result.comments();
		boolean hasComments = comments != null && (comments.isEmpty() == false);

		if (hasComments == false) {
			return result;
		}

		List<Long> commentIds = comments.stream().map(c -> c.commentId()).toList();
		Set<Long> parentCommentIds = postQueryRepository.findParentCommentsInCommentIds(commentIds);

		var finalComments = comments.stream()
			.map(c -> new PostViewProjection.SimpleCommentProjection(c.commentId(), c.profileId(), c.nickname(),
				c.contents(),
				parentCommentIds.contains(c.commentId())))
			.toList();

		return new PostViewProjection(
			result.postId(),
			result.profileId(),
			result.nickname(),
			result.content(),
			result.likeCount(),
			result.commentCount(),
			result.viewCount(),
			result.hasLikedPost(),
			result.createdAt(),
			result.modifiedAt(),
			result.files(),
			finalComments);
	}
}
