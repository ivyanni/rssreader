package io.github.ivyanni.rssreader.service;

import io.github.ivyanni.rssreader.config.FeedConfiguration;

/**
 * Service that manages feed updates in background thread.
 *
 * @author Ilia Vianni on 23.02.2019.
 */
public interface FeedUpdateService {

    /**
     * Add specified feed to scheduler.
     *
     * @param feedConfiguration the feed configuration
     */
    void scheduleFeedUpdate(FeedConfiguration feedConfiguration);

    /**
     * Reschedule feed update with new timeout.
     *
     * @param feedName   the feed name
     * @param newTimeout the new timeout
     */
    void rescheduleFeedUpdate(String feedName, Long newTimeout);

    /**
     * Remove feed from scheduler.
     *
     * @param feedName the feed name
     */
    void stopFeedUpdate(String feedName);

    /**
     * Add all existing feeds to scheduler.
     */
    void startService();

    /**
     * Remove all existing feeds from scheduler and shutdown it.
     */
    void stopService();
}
