package io.github.ivyanni.rssreader.service.tasks;

import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import io.github.ivyanni.rssreader.config.FeedConfiguration;
import io.github.ivyanni.rssreader.service.composers.OutputComposer;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;

/**
 * Performs feed update and saves feed entries to file.
 * @author Ilia Vianni on 23.02.2019.
 */
public class FeedProcessingTask implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(FeedProcessingTask.class);
    private FeedConfiguration feedConfiguration;

    public FeedProcessingTask(FeedConfiguration feedConfiguration) {
        this.feedConfiguration = feedConfiguration;
    }

    /**
     * Receives feed by source URL using ROME and saves content to drive.
     */
    @Override
    public void run() {
        try {
            SyndFeed feed = receiveFeed(feedConfiguration);
            String content = new OutputComposer().compose(feedConfiguration, feed);
            if (content.length() > 0) {
                File file = new File(feedConfiguration.getOutputFilename());
                saveContentToFile(file, content);
            }
        } catch (FeedException ex) {
            LOGGER.error("Exception was occurred while parsing feed data: {}", ex.getMessage(), ex);
        } catch (UnknownHostException ex) {
            LOGGER.error("Check your internet connection. Connection to {} was failed", ex.getMessage(), ex);
        } catch (IOException ex) {
            LOGGER.error("Exception was occurred while retrieving feed data: {}", ex.getMessage(), ex);
        }
    }

    /**
     * Receives feed by specified configuration using ROME.
     * @param feedConfiguration Feed configuration object
     * @return ROME feed object
     * @throws FeedException when ROME couldn't parse feed
     * @throws IOException when ROME couldn't receive feed from specified URL
     */
    private SyndFeed receiveFeed(FeedConfiguration feedConfiguration) throws FeedException, IOException {
        feedConfiguration.setLastRequestTime(Calendar.getInstance().getTime());
        SyndFeedInput input = new SyndFeedInput();
        return input.build(new XmlReader(feedConfiguration.getSourceUrl()));
    }

    /**
     * Saves formatted entries string to specified file.
     * @param file    File which will be used to save data
     * @param content Formatted feed entries string
     * @throws IOException when it couldn't append content to specified file
     */
    private synchronized void saveContentToFile(File file, String content) throws IOException {
        if (!file.exists()) {
            file.createNewFile();
        }
        FileUtils.write(file, content, StandardCharsets.UTF_8, true);
    }
}
