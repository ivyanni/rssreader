package io.github.ivyanni.rssreader.service;

import io.github.ivyanni.rssreader.config.ApplicationConfiguration;
import io.github.ivyanni.rssreader.config.FeedConfiguration;

import java.net.URL;

/**
 * @author Ilia Vianni on 23.02.2019.
 */
public interface FeedService {

    void addFeed(ApplicationConfiguration applicationConfiguration, FeedConfiguration feedConfiguration);

    void removeFeed(ApplicationConfiguration applicationConfiguration, URL feedUrl);

    void start(ApplicationConfiguration applicationConfiguration);

    void stop(ApplicationConfiguration applicationConfiguration);
}
