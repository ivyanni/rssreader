package io.github.ivyanni.rssreader.config;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Ilia Vianni on 23.02.2019.
 */
public class ApplicationConfiguration {
    private Integer corePoolSize;
    private Map<String, FeedConfiguration> feedConfigurations = new HashMap<>();

    public Integer getCorePoolSize() {
        return corePoolSize;
    }

    public void setCorePoolSize(Integer corePoolSize) {
        this.corePoolSize = corePoolSize;
    }

    public Map<String, FeedConfiguration> getFeedConfigurations() {
        return feedConfigurations;
    }

    public void setFeedConfigurations(Map<String, FeedConfiguration> feedConfigurations) {
        this.feedConfigurations = feedConfigurations;
    }
}
