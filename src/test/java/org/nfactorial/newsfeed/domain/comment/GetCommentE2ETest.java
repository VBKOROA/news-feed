package org.nfactorial.newsfeed.domain.comment;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Objects;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.nfactorial.newsfeed.common.dto.GlobalApiResponse;
import org.nfactorial.newsfeed.domain.comment.dto.request.WriteToCommentRequest;
import org.nfactorial.newsfeed.domain.comment.dto.response.GetCommentsFromCommentResponse;
import org.nfactorial.newsfeed.domain.comment.dto.response.GetCommentsFromPostResponse;
import org.nfactorial.newsfeed.domain.comment.dto.response.WriteToCommentResponse;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class GetCommentE2ETest extends CommentE2ETest {

    private String accessToken;
    private Long postId;

    @BeforeEach
    void setUp() {
        String email = signUp("test@email.com", "Password123!", "testuser");
        accessToken = login(email, "Password123!");
        postId = createPost(accessToken, "This is a test post.");
    }

    @Test
    @DisplayName("댓글 목록 조회 성공")
    void getComments_success() {
        // given
        // 댓글 2개 작성
        writeComment(accessToken, postId, "First comment");
        writeComment(accessToken, postId, "Second comment");

        // when
        ResponseEntity<GlobalApiResponse<GetCommentsFromPostResponse>> response = restTemplate.exchange(
            "/api/v1/posts/" + postId + "/comments",
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<>() {
            });

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        GlobalApiResponse<GetCommentsFromPostResponse> body = response.getBody();
        assertThat(body).isNotNull();
        List<GetCommentsFromPostResponse.SimpleComment> comments = body.data().comments();
        assertThat(comments).hasSize(2);
        assertThat(comments.get(0).content()).isEqualTo("Second comment");
        assertThat(comments.get(0).hasInnerComments()).isFalse();
        assertThat(comments.get(1).content()).isEqualTo("First comment");
        assertThat(comments.get(1).hasInnerComments()).isFalse();
    }

    @Test
    @DisplayName("대댓글이 있는 게시물 조회 성공")
    void getComments_withNestedComment_success() {
        // given
        Long firstCommentId = writeComment(accessToken, postId, "First comment");
        writeComment(accessToken, postId, "Second comment");
        writeReplyToComment(accessToken, firstCommentId, "This is a reply");

        // when
        ResponseEntity<GlobalApiResponse<GetCommentsFromPostResponse>> response = restTemplate.exchange(
            "/api/v1/posts/" + postId + "/comments",
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<>() {
            });

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        GlobalApiResponse<GetCommentsFromPostResponse> body = response.getBody();
        assertThat(body).isNotNull();
        List<GetCommentsFromPostResponse.SimpleComment> comments = body.data().comments();
        assertThat(comments).hasSize(2);

        GetCommentsFromPostResponse.SimpleComment firstComment = comments.stream()
            .filter(c -> c.commentId() == firstCommentId)
            .findFirst()
            .orElseThrow();

        assertThat(firstComment.content()).isEqualTo("First comment");
        assertThat(firstComment.hasInnerComments()).isTrue();
    }

    @Test
    @DisplayName("댓글 없는 게시물 조회 시 빈 목록 반환")
    void getComments_emptyList_whenNoComments() {
        // given - no comments

        // when
        ResponseEntity<GlobalApiResponse<GetCommentsFromPostResponse>> response = restTemplate.exchange(
            "/api/v1/posts/" + postId + "/comments",
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<>() {
            });

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        GlobalApiResponse<GetCommentsFromPostResponse> body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.data().comments()).isEmpty();
    }

    protected Long writeCommentToComment(String accessToken, Long parentCommentId, String content) {
        WriteToCommentRequest request = new WriteToCommentRequest(content);
        headers.setBearerAuth(accessToken);
        ResponseEntity<GlobalApiResponse<WriteToCommentResponse>> response = restTemplate.exchange(
            "/api/v1/comments/" + parentCommentId + "/comments",
            HttpMethod.POST,
            new HttpEntity<>(request, headers),
            new ParameterizedTypeReference<>() {
            });
        return Objects.requireNonNull(response.getBody()).data().id();
    }

    protected Long writeReplyToComment(String accessToken, Long parentCommentId, String content) {
        WriteToCommentRequest request = new WriteToCommentRequest(content);
        headers.setBearerAuth(accessToken);
        ResponseEntity<GlobalApiResponse<WriteToCommentResponse>> response = restTemplate.exchange(
            "/api/v1/comments/" + parentCommentId + "/comments",
            HttpMethod.POST,
            new HttpEntity<>(request, headers),
            new ParameterizedTypeReference<>() {
            });
        return Objects.requireNonNull(response.getBody()).data().id();
    }

    @Test
    @DisplayName("대댓글 조회 성공")
    void getCommentsFromComment() {
        // given
        Long postId = createPost(accessToken, "post content");
        Long parentCommentId = writeComment(accessToken, postId, "parent comment");

        writeCommentToComment(accessToken, parentCommentId, "child comment 1");
        writeCommentToComment(accessToken, parentCommentId, "child comment 2");

        // when
        ResponseEntity<GlobalApiResponse<GetCommentsFromCommentResponse>> response = restTemplate.exchange(
            "/api/v1/comments/" + parentCommentId + "/comments",
            HttpMethod.GET,
            new HttpEntity<>(headers),
            new ParameterizedTypeReference<>() {
            });

        // then
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        GetCommentsFromCommentResponse responseBody = Objects.requireNonNull(response.getBody()).data();
        assertThat(responseBody.comments()).hasSize(2);
        assertThat(responseBody.comments().get(0).content()).isEqualTo("child comment 1");
        assertThat(responseBody.comments().get(1).content()).isEqualTo("child comment 2");
    }
}