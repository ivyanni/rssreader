package io.github.ivyanni.rssreader.config;

import java.util.HashMap;
import java.util.Map;

/**
 * Main application configuration model.
 * @author Ilia Vianni on 23.02.2019.
 */
public class ApplicationConfiguration {
    private Integer corePoolSize;
    private Map<String, FeedConfiguration> feedConfigurations;

    public ApplicationConfiguration() {
        this.corePoolSize = 4;
        this.feedConfigurations = new HashMap<>();
    }

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
