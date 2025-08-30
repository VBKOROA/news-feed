package org.nfactorial.newsfeed.common.code;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ErrorCode implements GlobalResponseCode {
	NOT_AUTHENTICATED("AUTH-401", "인증이 필요합니다.", HttpStatus.UNAUTHORIZED),
	EMAIL_DUPLICATED("AUTH-400", "중복되는 이메일입니다.", HttpStatus.BAD_REQUEST),
	NICKNAME_DUPLICATED("AUTH-400-1", "중복되는 닉네임입니다.", HttpStatus.BAD_REQUEST),
	INVALID_TOKEN("AUTH-400-2", "유효하지 않은 토큰입니다.", HttpStatus.BAD_REQUEST),
	LOGIN_FAILED("AUTH-401-1", "로그인에 실패하였습니다.", HttpStatus.UNAUTHORIZED),
	ACCOUNT_NOT_FOUND("AUTH-404", "계정을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
	PASSWORD_DOESNT_MATCH("AUTH-401-2", "비밀번호가 일치하지 않습니다.", HttpStatus.UNAUTHORIZED),
	NEW_PASSWORD_SAME_AS_CURRENT("AUTH-400-3", "새로운 비밀번호가 현재 비밀번호와 같습니다.", HttpStatus.BAD_REQUEST),
	PROFILE_NOT_FOUND("PROFILE-404", "프로필을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),

	//Post
	POST_NOT_FOUND("POST-404", "해당 게시글을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
	POST_ACCESS_DENIED("POST-403", "게시글에 대한 권한이 없습니다.", HttpStatus.FORBIDDEN),

	// Interaction
	LIKE_ALREADY_EXISTS("INTR-409-1", "이미 좋아요를 누른 게시물입니다.", HttpStatus.CONFLICT),
	LIKE_NOT_FOUND("INTR-404-1", "게시글에 대한 좋아요 기록을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
	CANNOT_FOLLOW_SELF("INTR-400", "자기 자신을 팔로우할 수 없습니다.", HttpStatus.BAD_REQUEST),
	FOLLOWING_ALREADY_EXISTS("INTR-409-2", "이미 팔로우한 사용자입니다.", HttpStatus.CONFLICT),
	FOLLOWING_NOT_FOUND("INTR-404-2", "팔로우한 기록을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),

	// Comment
	COMMENT_NOT_FOUND("COMMENT-404", "댓글을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
	COMMENT_NOT_YOURS("COMMENT-403", "본인의 댓글이 아닙니다.", HttpStatus.FORBIDDEN),

	// Upload
	FILE_SAVE_FAILED("UPLOAD-500", "서버가 파일을 뱉어냈습니다...", HttpStatus.INTERNAL_SERVER_ERROR),
	EXT_NOT_ALLOWED("UPLOAD-400", "허용되지 않은 확장자입니다.", HttpStatus.BAD_REQUEST),
	FILE_SIZE_TOO_BIG("UPLOAD-400", "파일 크기는 10MB 까지 입니다.", HttpStatus.BAD_REQUEST),
	FILE_NOT_FOUND("UPLOAD-404", "파일을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
	UPLOAD_NOT_FOUND("UPLOAD-404-1", "업로드를 찾을 수 없습니다.", HttpStatus.NOT_FOUND);

	private final String code;
	private final String message;
	private final HttpStatus httpStatus;
}
