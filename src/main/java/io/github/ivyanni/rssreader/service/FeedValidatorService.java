package io.github.ivyanni.rssreader.service;

import io.github.ivyanni.rssreader.config.FeedConfiguration;

/**
 * @author Ilia Vianni on 01.03.2019.
 */
public interface FeedValidatorService {

    boolean validateFeed(FeedConfiguration feedConfiguration);
}
