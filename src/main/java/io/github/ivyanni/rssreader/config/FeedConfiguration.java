package io.github.ivyanni.rssreader.config;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

/**
 * Feed's configuration model.
 *
 * @author Ilia Vianni on 23.02.2019.
 */
public class FeedConfiguration {
    private Long delay;
    private String outputFilename;
    private Long chunkSize;
    private URL sourceUrl;
    private List<String> outputParams;
    private Date lastRequestTime;
    private Date lastMessageTime;
    @JsonIgnore
    private ScheduledFuture savedFuture;

    public FeedConfiguration() {
    }

    public FeedConfiguration(URL sourceUrl, String outputFilename) {
        this.sourceUrl = sourceUrl;
        this.outputFilename = outputFilename;
    }

    public Long getDelay() {
        return delay;
    }

    public void setDelay(Long delay) {
        this.delay = delay;
    }

    public String getOutputFilename() {
        return outputFilename;
    }

    public void setOutputFilename(String outputFilename) {
        this.outputFilename = outputFilename;
    }

    public Long getChunkSize() {
        return chunkSize;
    }

    public void setChunkSize(Long chunkSize) {
        this.chunkSize = chunkSize;
    }

    public URL getSourceUrl() {
        return sourceUrl;
    }

    public void setSourceUrl(URL sourceUrl) {
        this.sourceUrl = sourceUrl;
    }

    public List<String> getOutputParams() {
        return outputParams;
    }

    public void setOutputParams(List<String> outputParams) {
        this.outputParams = outputParams;
    }

    public Date getLastRequestTime() {
        return lastRequestTime;
    }

    public void setLastRequestTime(Date lastRequestTime) {
        this.lastRequestTime = lastRequestTime;
    }

    public ScheduledFuture getSavedFuture() {
        return savedFuture;
    }

    public void setSavedFuture(ScheduledFuture savedFuture) {
        this.savedFuture = savedFuture;
    }

    public Date getLastMessageTime() {
        return lastMessageTime;
    }

    public void setLastMessageTime(Date lastMessageTime) {
        this.lastMessageTime = lastMessageTime;
    }
}
