package org.nfactorial.newsfeed.domain.post.repository;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.nfactorial.newsfeed.domain.comment.entity.QComment;
import org.nfactorial.newsfeed.domain.interaction.entity.QLike;
import org.nfactorial.newsfeed.domain.post.dto.projection.PostViewProjection.*;
import org.nfactorial.newsfeed.domain.post.entity.QPost;
import org.nfactorial.newsfeed.domain.profile.entity.QProfile;
import org.nfactorial.newsfeed.domain.upload.entity.QUpload;
import org.springframework.stereotype.Repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class PostQueryRepository {
    private final JPAQueryFactory queryFactory;

    public Optional<PostDetailProjection> findPostDetailByPostId(long postId, long currentUserId) {
        QPost post = QPost.post;
        QProfile postAuthor = new QProfile("postAuthor");
        QLike like = QLike.like;
        QComment comment = QComment.comment;

        return Optional.ofNullable(queryFactory.select(Projections.constructor(
            PostDetailProjection.class,
            post.id,
            postAuthor.id,
            postAuthor.nickname,
            post.content,
            post.likeCount,
            JPAExpressions.select(comment.id.countDistinct())
                .from(comment)
                .where(comment.post.id.eq(postId)),
            post.viewCount,
            JPAExpressions.selectOne()
                .from(like)
                .where(like.post.id.eq(postId).and(like.profile.id.eq(currentUserId)))
                .exists(),
            post.createdAt,
            post.modifiedAt))
            .from(post)
            .join(post.profile, postAuthor)
            .where(post.id.eq(postId))
            .fetchOne());
    }

    public List<SimpleFileProjection> findAllSimpleFileByPostId(long postId) {
        QUpload upload = QUpload.upload;

        return queryFactory.select(Projections.constructor(
            SimpleFileProjection.class,
            upload.id,
            upload.publicName)).from(upload)
            .join(upload.post)
            .where(upload.post.id.eq(postId))
            .fetch();
    }

    public List<SimpleCommentProjection> findAllSimpleCommentByPostId(long postId) {
        QPost post = QPost.post;
        QComment comment = QComment.comment;
        QProfile authorProfile = new QProfile("authorProfile");

        return queryFactory.select(Projections.constructor(
            SimpleCommentProjection.class,
            comment.id,
            authorProfile.id,
            authorProfile.nickname,
            comment.content,
            Expressions.nullExpression(Boolean.class)))
            .from(comment)
            .join(comment.profile, authorProfile)
            .join(comment.post, post)
            .where(comment.post.id.eq(postId))
            .fetch();
    }

    public Set<Long> findParentCommentsInCommentIds(List<Long> commentIds) {
        QComment comment = QComment.comment;
        return new HashSet<Long>(
            queryFactory.select(comment.parentComment.id)
                .from(comment)
                .where(comment.parentComment.id.in(commentIds))
                .distinct()
                .fetch());
    }
}
