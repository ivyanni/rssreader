package io.github.ivyanni.rssreader.config;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

/**
 * Feed configuration pojo
 *
 * @author Ilia Vianni on 23.02.2019.
 */
public class FeedConfiguration {
    private Long timeout;
    private String filename;
    private Long itemsAmount;
    private URL feedUrl;
    private List<String> params;
    private Date lastRequestTime;
    private Date lastSavedMessageDate;
    @JsonIgnore
    private ScheduledFuture scheduledFuture;

    public Long getTimeout() {
        return timeout;
    }

    public void setTimeout(Long timeout) {
        this.timeout = timeout;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public Long getItemsAmount() {
        return itemsAmount;
    }

    public void setItemsAmount(Long itemsAmount) {
        this.itemsAmount = itemsAmount;
    }

    public URL getFeedUrl() {
        return feedUrl;
    }

    public void setFeedUrl(URL feedUrl) {
        this.feedUrl = feedUrl;
    }

    public List<String> getParams() {
        return params;
    }

    public void setParams(List<String> params) {
        this.params = params;
    }

    public Date getLastRequestTime() {
        return lastRequestTime;
    }

    public void setLastRequestTime(Date lastRequestTime) {
        this.lastRequestTime = lastRequestTime;
    }

    public ScheduledFuture getScheduledFuture() {
        return scheduledFuture;
    }

    public void setScheduledFuture(ScheduledFuture scheduledFuture) {
        this.scheduledFuture = scheduledFuture;
    }

    public Date getLastSavedMessageDate() {
        return lastSavedMessageDate;
    }

    public void setLastSavedMessageDate(Date lastSavedMessageDate) {
        this.lastSavedMessageDate = lastSavedMessageDate;
    }
}
