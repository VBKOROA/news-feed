package org.nfactorial.newsfeed.domain.post.dto.result;

import java.util.List;

import org.nfactorial.newsfeed.domain.post.dto.projection.PostViewProjection.*;

public record ViewPostResult(
    PostDetailProjection post,
    List<SimpleFileProjection> files,
    List<SimpleCommentProjection> comments) {
}
