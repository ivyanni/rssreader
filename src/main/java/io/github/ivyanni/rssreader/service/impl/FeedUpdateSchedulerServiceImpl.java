package io.github.ivyanni.rssreader.service.impl;

import io.github.ivyanni.rssreader.config.ApplicationConfiguration;
import io.github.ivyanni.rssreader.config.FeedConfiguration;
import io.github.ivyanni.rssreader.service.FeedUpdateSchedulerService;
import io.github.ivyanni.rssreader.service.tasks.FeedUpdateTask;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author Ilia Vianni on 23.02.2019.
 */
public class FeedUpdateSchedulerServiceImpl implements FeedUpdateSchedulerService {
    private ApplicationConfiguration applicationConfiguration;
    private ScheduledExecutorService executor;

    public FeedUpdateSchedulerServiceImpl(ApplicationConfiguration applicationConfiguration) {
        this.applicationConfiguration = applicationConfiguration;
        this.executor = Executors.newScheduledThreadPool(applicationConfiguration.getCorePoolSize());
    }

    @Override
    public void scheduleFeedUpdate(FeedConfiguration feedConfiguration) {
        ScheduledFuture future = executor.scheduleAtFixedRate(new FeedUpdateTask(feedConfiguration),
                0, feedConfiguration.getDelay(), TimeUnit.SECONDS);
        feedConfiguration.setSavedFuture(future);
    }

    @Override
    public void rescheduleFeedUpdate(String feedName, Long newDelay) {
        FeedConfiguration feedConfiguration = applicationConfiguration.getFeedConfigurations().get(feedName);
        ScheduledFuture future = feedConfiguration.getSavedFuture();
        future.cancel(false);
        Date lastRequestTime = feedConfiguration.getLastRequestTime();
        Long initialDelay = calculateInitialDelay(lastRequestTime, newDelay);
        future = executor.scheduleAtFixedRate(new FeedUpdateTask(feedConfiguration),
                initialDelay, newDelay, TimeUnit.SECONDS);
        feedConfiguration.setSavedFuture(future);
    }

    @Override
    public void stopFeedUpdate(String feedName) {
        FeedConfiguration feedConfiguration = applicationConfiguration.getFeedConfigurations().get(feedName);
        feedConfiguration.getSavedFuture().cancel(true);
    }

    @Override
    public void scheduleAllFeedUpdates() {
        applicationConfiguration.getFeedConfigurations().values().forEach(feedConfiguration -> {
            long delay = feedConfiguration.getDelay();
            Date lastRequestTime = feedConfiguration.getLastRequestTime();
            Long initialDelay = calculateInitialDelay(lastRequestTime, delay);
            ScheduledFuture future = executor.scheduleAtFixedRate(new FeedUpdateTask(feedConfiguration),
                    initialDelay, delay, TimeUnit.SECONDS);
            feedConfiguration.setSavedFuture(future);
        });
    }

    @Override
    public void shutdownUpdates() {
        executor.shutdown();
    }

    private Long calculateInitialDelay(Date lastRequestTime, Long delay) {
        long initialDelay = 0;
        if (lastRequestTime != null) {
            long duration = Math.abs(Duration.between(Instant.now(),
                    lastRequestTime.toInstant()).toSeconds());
            initialDelay = duration > delay ? 0 : delay - duration;
        }
        return initialDelay;
    }
}
