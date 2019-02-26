package io.github.ivyanni.rssreader.service.impl;

import io.github.ivyanni.rssreader.config.ApplicationConfiguration;
import io.github.ivyanni.rssreader.config.FeedConfiguration;
import io.github.ivyanni.rssreader.service.FeedUpdateService;
import io.github.ivyanni.rssreader.service.tasks.FeedRetrieverTask;

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
public class FeedUpdateServiceImpl implements FeedUpdateService {
    private ApplicationConfiguration applicationConfiguration;
    private ScheduledExecutorService executor;

    public FeedUpdateServiceImpl(ApplicationConfiguration applicationConfiguration) {
        this.applicationConfiguration = applicationConfiguration;
        initExecutor();
    }

    @Override
    public void scheduleFeedUpdate(String feedName, FeedConfiguration feedConfiguration) {
        ScheduledFuture future = executor.scheduleAtFixedRate(new FeedRetrieverTask(feedConfiguration), 0, feedConfiguration.getTimeout(), TimeUnit.SECONDS);
        feedConfiguration.setScheduledFuture(future);
    }

    @Override
    public void rescheduleFeedUpdate(String feedName, Long newTimeout) {
        FeedConfiguration feedConfiguration = applicationConfiguration.getFeedConfigurations().get(feedName);
        ScheduledFuture future = feedConfiguration.getScheduledFuture();
        future.cancel(false);
        Date lastRequestTime = feedConfiguration.getLastRequestTime();
        Long initialDelay = calculateInitialDelay(lastRequestTime, newTimeout);
        future = executor.scheduleAtFixedRate(new FeedRetrieverTask(feedConfiguration), initialDelay, newTimeout, TimeUnit.SECONDS);
        feedConfiguration.setScheduledFuture(future);
    }

    @Override
    public void stopFeedUpdate(String feedName) {
        FeedConfiguration feedConfiguration = applicationConfiguration.getFeedConfigurations().get(feedName);
        feedConfiguration.getScheduledFuture().cancel(true);
    }

    @Override
    public void startService() {
        applicationConfiguration.getFeedConfigurations().values().forEach(feedConfiguration -> {
            long timeout = feedConfiguration.getTimeout();
            Date lastRequestTime = feedConfiguration.getLastRequestTime();
            Long initialDelay = calculateInitialDelay(lastRequestTime, timeout);
            ScheduledFuture future = executor.scheduleAtFixedRate(new FeedRetrieverTask(feedConfiguration), initialDelay, timeout, TimeUnit.SECONDS);
            feedConfiguration.setScheduledFuture(future);
        });
    }

    @Override
    public void stopService() {
        executor.shutdown();
    }

    private Long calculateInitialDelay(Date lastRequestTime, Long timeout) {
        long initialDelay = 0;
        if (lastRequestTime != null) {
            long duration = Math.abs(Duration.between(Instant.now(), lastRequestTime.toInstant()).toSeconds());
            initialDelay = duration > timeout ? 0 : timeout - duration;
        }
        return initialDelay;
    }

    private void initExecutor() {
        if (applicationConfiguration.getCorePoolSize() == null) {
            applicationConfiguration.setCorePoolSize(4);
        }
        this.executor = Executors.newScheduledThreadPool(applicationConfiguration.getCorePoolSize());
    }
}
