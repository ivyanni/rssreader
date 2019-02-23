package io.github.ivyanni.rssreader.service.impl;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import io.github.ivyanni.rssreader.config.ApplicationConfiguration;
import io.github.ivyanni.rssreader.config.FeedConfiguration;
import io.github.ivyanni.rssreader.service.FeedService;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author Ilia Vianni on 23.02.2019.
 */
public class FeedServiceImpl implements FeedService {
    private ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    @Override
    public void addFeed(ApplicationConfiguration applicationConfiguration, FeedConfiguration feedConfiguration) {
        ScheduledFuture future = executor.scheduleAtFixedRate(new WriteDataTask(feedConfiguration), 0, feedConfiguration.getTimeout(), TimeUnit.SECONDS);
        feedConfiguration.setScheduledFuture(future);
        applicationConfiguration.getFeedConfigurationList().add(feedConfiguration);
    }

    @Override
    public void removeFeed(ApplicationConfiguration applicationConfiguration, URL feedUrl) {
        FeedConfiguration feedConfiguration = applicationConfiguration.getFeedConfigurationList().stream().filter(config -> config.getFeedUrl().equals(feedUrl)).findFirst().orElseThrow();
        feedConfiguration.getScheduledFuture().cancel(true);
        applicationConfiguration.getFeedConfigurationList().remove(feedConfiguration);
    }

    @Override
    public void start(ApplicationConfiguration applicationConfiguration) {
        applicationConfiguration.getFeedConfigurationList().forEach(feedConfiguration -> {
            long timeout = feedConfiguration.getTimeout();
            Date lastRequestTime = feedConfiguration.getLastRequestTime();
            ScheduledFuture future;
            long initialDelay = 0;
            if(lastRequestTime != null) {
                long duration = Math.abs(Duration.between(Instant.now(), lastRequestTime.toInstant()).toSeconds());
                initialDelay = duration > timeout ? 0 : timeout - duration;
            }
            future = executor.scheduleAtFixedRate(new WriteDataTask(feedConfiguration), initialDelay, timeout, TimeUnit.SECONDS);
            feedConfiguration.setScheduledFuture(future);
        });
    }

    @Override
    public void stop(ApplicationConfiguration applicationConfiguration) {
        applicationConfiguration.getFeedConfigurationList().forEach(feedConfiguration -> feedConfiguration.setScheduledFuture(null));
        executor.shutdown();
    }

    private class WriteDataTask implements Runnable {
        private FeedConfiguration feedConfiguration;

        WriteDataTask(FeedConfiguration feedConfiguration) {
            this.feedConfiguration = feedConfiguration;
        }

        @Override
        public void run() {
            try {
                feedConfiguration.setLastRequestTime(Calendar.getInstance().getTime());
                SyndFeedInput input = new SyndFeedInput();
                SyndFeed feed = input.build(new XmlReader(feedConfiguration.getFeedUrl()));
                StringBuilder str = new StringBuilder();
                for (SyndEntry entry : feed.getEntries()) {
                    str.append(Calendar.getInstance().getTime().toString()).append(" - ").append(entry.getPublishedDate()).append(": ").append(entry.getTitle()).append(System.getProperty("line.separator"));
                }
                File file = new File(feedConfiguration.getFilename());
                saveToFile(file, str.toString());
            } catch (FeedException | IOException e) {
                e.printStackTrace();
            }
        }

        private void saveToFile(File file, String str) {
            synchronized (this) {
                try {
                    if (!file.exists()) {
                        file.createNewFile();
                    }
                    FileUtils.write(file, str, StandardCharsets.UTF_8, true);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
}
