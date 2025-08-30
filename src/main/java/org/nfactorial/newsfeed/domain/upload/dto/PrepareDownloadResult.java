package org.nfactorial.newsfeed.domain.upload.dto;

import org.springframework.core.io.Resource;

public record PrepareDownloadResult(
    Resource resource,
    String fileName) {

}
