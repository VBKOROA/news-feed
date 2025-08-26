package org.nfactorial.newsfeed.domain.comment.dto.projection;

public interface ViewCommentFromPostProjection {
    long getCommentId();

    long getProfileId();

    String getNickname();

    String getContent();

    boolean getHasInnerComments();
}
