package org.nfactorial.newsfeed.domain.comment.repository;

import java.util.List;

import org.nfactorial.newsfeed.domain.comment.dto.projection.ViewCommentFromPostProjection;
import org.nfactorial.newsfeed.domain.comment.entity.Comment;
import org.nfactorial.newsfeed.domain.post.entity.Post;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CommentRepository extends JpaRepository<Comment, Long> {
	@EntityGraph(attributePaths = {"profile"})
	List<Comment> findAllByPost(Post post);

	@Query("""
		SELECT
			c.id AS commentId,
		    c.profile.id AS profileId,
			c.profile.nickname as nickname,
		    c.content AS content,
		    (COUNT(cc.id) > 0) AS hasInnerComments
		FROM Comment c
		LEFT JOIN Comment cc ON cc.parentComment.id = c.id
		WHERE c.post = :post AND c.parentComment IS NULL
		GROUP BY c.id
		ORDER BY c.createdAt DESC
		""")
	List<ViewCommentFromPostProjection> viewCommentFromPost(Post post);
}
