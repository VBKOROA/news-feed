package org.nfactorial.newsfeed.domain.post.dto.response;

import java.util.List;

import org.nfactorial.newsfeed.domain.post.dto.projection.PostViewProjection.*;
import org.nfactorial.newsfeed.domain.post.dto.result.ViewPostResult;

public record ViewPostResponse(
    PostDetailProjection post,
    List<SimpleCommentProjection> comments,
    List<SimpleFileProjection> files) {

    public static ViewPostResponse of(ViewPostResult result) {
        return new ViewPostResponse(result.post(), result.comments(), result.files());
    }
}
