package io.github.ivyanni.rssreader.service;

import io.github.ivyanni.rssreader.model.FeedConfiguration;

/**
 * Manages scheduling of feed updates.
 *
 * @author Ilia Vianni on 23.02.2019.
 */
public interface FeedUpdateSchedulerService {

    /**
     * Adds specified feed to scheduler.
     *
     * @param feedConfiguration Feed's configuration object
     */
    void scheduleFeedUpdate(FeedConfiguration feedConfiguration);

    /**
     * Reschedules feed's update with new delay.
     *
     * @param feedName Feed's name
     * @param newDelay Feed's new delay
     */
    void rescheduleFeedUpdate(String feedName, Long newDelay);

    /**
     * Removes feed from scheduler.
     *
     * @param feedName Feed's name
     */
    void stopFeedUpdate(String feedName);

    /**
     * Adds all existing in application's configuration feeds to scheduler.
     */
    void scheduleAllFeedUpdates();

    /**
     * Removes all existing in application's configuration feeds from scheduler and shutdowns it.
     */
    void shutdownUpdates();
}
