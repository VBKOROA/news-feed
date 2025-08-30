package org.nfactorial.newsfeed.domain.comment;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import java.util.Objects;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.nfactorial.newsfeed.common.dto.GlobalApiResponse;
import org.nfactorial.newsfeed.domain.comment.dto.request.WriteCommentToPostRequest;
import org.nfactorial.newsfeed.domain.comment.dto.request.WriteToCommentRequest;
import org.nfactorial.newsfeed.domain.comment.dto.response.WriteCommentToPostResponse;
import org.nfactorial.newsfeed.domain.comment.dto.response.WriteToCommentResponse;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class WriteCommentE2ETest extends CommentE2ETest {

    private String accessToken;
    private Long postId;

    @BeforeEach
    void setUp() {
        String email = signUp("test@email.com", "Password123!", "testuser");
        accessToken = login(email, "Password123!");
        postId = createPost(accessToken, "This is a test post.");
    }

    @Test
    @DisplayName("댓글 작성 성공")
    void writeComment_success() {
        // given
        WriteCommentToPostRequest request = new WriteCommentToPostRequest("This is a test comment.");
        headers.setBearerAuth(accessToken);

        // when
        ResponseEntity<GlobalApiResponse<WriteCommentToPostResponse>> response = restTemplate.exchange(
            "/api/v1/posts/" + postId + "/comments",
            HttpMethod.POST,
            new HttpEntity<>(request, headers),
            new ParameterizedTypeReference<>() {
            });

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(Objects.requireNonNull(response.getBody()).data().content()).isEqualTo("This is a test comment.");
        assertThat(commentRepository.count()).isEqualTo(1);
    }

    @Test
    @DisplayName("댓글 작성 실패 - 인증되지 않은 사용자")
    void writeComment_fail_unauthorized() {
        // given
        WriteCommentToPostRequest request = new WriteCommentToPostRequest("This is a test comment.");

        // when
        ResponseEntity<GlobalApiResponse<Void>> response = restTemplate.exchange(
            "/api/v1/posts/" + postId + "/comments",
            HttpMethod.POST,
            new HttpEntity<>(request), // No token in header
            new ParameterizedTypeReference<>() {
            });

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    @DisplayName("댓글 작성 실패 - 존재하지 않는 게시물")
    void writeComment_fail_postNotFound() {
        // given
        long nonExistentPostId = 999L;
        WriteCommentToPostRequest request = new WriteCommentToPostRequest("This is a test comment.");
        headers.setBearerAuth(accessToken);

        // when
        ResponseEntity<GlobalApiResponse<Map<String, String>>> response = restTemplate.exchange(
            "/api/v1/posts/" + nonExistentPostId + "/comments",
            HttpMethod.POST,
            new HttpEntity<>(request, headers),
            new ParameterizedTypeReference<>() {
            });

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("댓글 작성 실패 - 내용이 없는 경우")
    void writeComment_fail_emptyContent() {
        // given
        WriteCommentToPostRequest request = new WriteCommentToPostRequest(""); // Empty content
        headers.setBearerAuth(accessToken);

        // when
        ResponseEntity<GlobalApiResponse<Map<String, String>>> response = restTemplate.exchange(
            "/api/v1/posts/" + postId + "/comments",
            HttpMethod.POST,
            new HttpEntity<>(request, headers),
            new ParameterizedTypeReference<>() {
            });

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("댓글에 댓글 작성 성공")
    void writeCommentToComment_success() {
        // given
        WriteCommentToPostRequest firstCommentRequest = new WriteCommentToPostRequest("This is the first comment.");
        headers.setBearerAuth(accessToken);

        ResponseEntity<GlobalApiResponse<WriteCommentToPostResponse>> firstCommentResponse = restTemplate.exchange(
            "/api/v1/posts/" + postId + "/comments",
            HttpMethod.POST,
            new HttpEntity<>(firstCommentRequest, headers),
            new ParameterizedTypeReference<>() {
            });

        assertThat(firstCommentResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        Long parentCommentId = Objects.requireNonNull(firstCommentResponse.getBody()).data().id();

        WriteToCommentRequest replyCommentRequest = new WriteToCommentRequest("This is a reply to the first comment.");

        // when
        ResponseEntity<GlobalApiResponse<WriteToCommentResponse>> replyCommentResponse = restTemplate.exchange(
            "/api/v1/comments/" + parentCommentId + "/comments",
            HttpMethod.POST,
            new HttpEntity<>(replyCommentRequest, headers),
            new ParameterizedTypeReference<>() {
            });

        // then
        assertThat(replyCommentResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(replyCommentResponse.getBody()).isNotNull();
        assertThat(Objects.requireNonNull(replyCommentResponse.getBody()).data().content()).isEqualTo("This is a reply to the first comment.");
        assertThat(commentRepository.count()).isEqualTo(2);
    }
}