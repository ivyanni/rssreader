package io.github.ivyanni.rssreader.config;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Ilia Vianni on 23.02.2019.
 */
public class ApplicationConfiguration {
    private List<FeedConfiguration> feedConfigurationList = new ArrayList<>();

    public List<FeedConfiguration> getFeedConfigurationList() {
        return feedConfigurationList;
    }

    public void setFeedConfigurationList(List<FeedConfiguration> feedConfigurationList) {
        this.feedConfigurationList = feedConfigurationList;
    }
}
