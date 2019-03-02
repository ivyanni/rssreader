package io.github.ivyanni.rssreader.service;

import io.github.ivyanni.rssreader.model.FeedConfiguration;

/**
 * Validates added or changed feed.
 *
 * @author Ilia Vianni on 01.03.2019.
 */
public interface FeedValidatorService {

    /**
     * Validates passed feed's configuration.
     *
     * @param feedConfiguration Feed's configuration
     * @return validation result
     */
    boolean validateFeed(FeedConfiguration feedConfiguration);
}
