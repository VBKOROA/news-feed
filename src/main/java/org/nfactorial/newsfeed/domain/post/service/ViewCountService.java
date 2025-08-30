package org.nfactorial.newsfeed.domain.post.service;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class ViewCountService {
    private final Set<String> viewHistory = ConcurrentHashMap.newKeySet();

    public void addView(long postId, long profileId) {
        viewHistory.add(idsToKey(postId, profileId));
    }

    public boolean isFirstView(long postId, long profileId) {
        return viewHistory.contains(idsToKey(postId, profileId)) == false;
    }

    private String idsToKey(long postId, long profileId) {
        return postId + ":" + profileId;
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void clearViewHistory() {
        viewHistory.clear();
    }
}
