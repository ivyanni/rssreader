package io.github.ivyanni.rssreader.service.impl;

import io.github.ivyanni.rssreader.config.ApplicationConfiguration;
import io.github.ivyanni.rssreader.config.FeedConfiguration;
import io.github.ivyanni.rssreader.service.FeedService;
import io.github.ivyanni.rssreader.service.tasks.RetrieveFeedTask;

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
public class FeedServiceImpl implements FeedService {
    private ApplicationConfiguration applicationConfiguration;
    private ScheduledExecutorService executor;

    public FeedServiceImpl(ApplicationConfiguration applicationConfiguration) {
        this.applicationConfiguration = applicationConfiguration;
        initExecutor();
    }

    @Override
    public void addFeed(String feedName, FeedConfiguration feedConfiguration) {
        ScheduledFuture future = executor.scheduleAtFixedRate(new RetrieveFeedTask(feedConfiguration), 0, feedConfiguration.getTimeout(), TimeUnit.SECONDS);
        feedConfiguration.setScheduledFuture(future);
        applicationConfiguration.getFeedConfigurations().put(feedName, feedConfiguration);
    }

    @Override
    public void removeFeed(String feedName) {
        FeedConfiguration feedConfiguration = applicationConfiguration.getFeedConfigurations().get(feedName);
        feedConfiguration.getScheduledFuture().cancel(true);
        applicationConfiguration.getFeedConfigurations().remove(feedName);
    }

    @Override
    public void start() {
        applicationConfiguration.getFeedConfigurations().values().forEach(feedConfiguration -> {
            long timeout = feedConfiguration.getTimeout();
            Date lastRequestTime = feedConfiguration.getLastRequestTime();
            long initialDelay = 0;
            if (lastRequestTime != null) {
                long duration = Math.abs(Duration.between(Instant.now(), lastRequestTime.toInstant()).toSeconds());
                initialDelay = duration > timeout ? 0 : timeout - duration;
            }
            ScheduledFuture future = executor.scheduleAtFixedRate(new RetrieveFeedTask(feedConfiguration), initialDelay, timeout, TimeUnit.SECONDS);
            feedConfiguration.setScheduledFuture(future);
        });
    }

    @Override
    public void stop() {
        executor.shutdown();
    }

    private void initExecutor() {
        if (applicationConfiguration.getCorePoolSize() == null) {
            applicationConfiguration.setCorePoolSize(4);
        }
        this.executor = Executors.newScheduledThreadPool(applicationConfiguration.getCorePoolSize());
    }
}
