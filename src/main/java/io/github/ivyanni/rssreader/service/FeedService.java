package io.github.ivyanni.rssreader.service;

import io.github.ivyanni.rssreader.config.FeedConfiguration;

import java.net.URL;

/**
 * @author Ilia Vianni on 23.02.2019.
 */
public interface FeedService {

    void addFeed(FeedConfiguration feedConfiguration);

    void removeFeed(URL feedUrl);

    void start();

    void stop();
}
