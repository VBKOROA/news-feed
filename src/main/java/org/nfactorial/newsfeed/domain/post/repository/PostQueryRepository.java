package org.nfactorial.newsfeed.domain.post.repository;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.nfactorial.newsfeed.domain.comment.entity.QComment;
import org.nfactorial.newsfeed.domain.interaction.entity.QLike;
import org.nfactorial.newsfeed.domain.post.dto.projection.PostViewProjection;
import org.nfactorial.newsfeed.domain.post.entity.QPost;
import org.nfactorial.newsfeed.domain.profile.entity.QProfile;
import org.nfactorial.newsfeed.domain.upload.entity.QUpload;
import org.springframework.stereotype.Repository;

import com.querydsl.core.group.GroupBy;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class PostQueryRepository {
    private final JPAQueryFactory queryFactory;

    public Optional<PostViewProjection> findViewPostProjectionByPostId(long postId, long currentUserId) {
        QPost post = QPost.post;
        QProfile postAuthor = new QProfile("postAuthor");
        QUpload upload = QUpload.upload;
        QComment comment = QComment.comment;
        QProfile commentAuthor = new QProfile("commentAuthor");
        QLike like = QLike.like;

        Map<Long, PostViewProjection> result = queryFactory
            .from(post)
            .join(post.profile, postAuthor)
            .leftJoin(upload).on(upload.post.id.eq(postId))
            .leftJoin(comment).on(comment.post.id.eq(postId))
            .leftJoin(comment.profile, commentAuthor)
            .where(post.id.eq(postId))
            .groupBy(post.id, postAuthor.id, postAuthor.nickname, post.content,
                post.likeCount, post.viewCount, post.createdAt, post.modifiedAt, upload.id, upload.publicName,
                comment.id, commentAuthor.id, commentAuthor.nickname, comment.content)
            .transform(
                GroupBy.groupBy(post.id).as(
                    Projections.constructor(
                        PostViewProjection.class,
                        post.id,
                        postAuthor.id,
                        postAuthor.nickname,
                        post.content,
                        post.likeCount,
                        comment.id.countDistinct().longValue(),
                        post.viewCount,
                        JPAExpressions.selectOne()
                            .from(like)
                            .where(like.post.id.eq(postId).and(like.profile.id.eq(currentUserId)))
                            .exists(),
                        post.createdAt,
                        post.modifiedAt,
                        GroupBy.list(
                            Projections.constructor(
                                PostViewProjection.SimpleFileProjection.class,
                                upload.id,
                                upload.publicName).skipNulls()),
                        GroupBy.list(
                            Projections.constructor(
                                PostViewProjection.SimpleCommentProjection.class,
                                comment.id,
                                commentAuthor.id,
                                commentAuthor.nickname,
                                comment.content,
                                Expressions.nullExpression(Boolean.class)).skipNulls()))));
        return Optional.ofNullable(result.get(postId));
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
