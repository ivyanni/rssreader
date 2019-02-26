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
 * Performs feed update and writes it to file.
 *
 * @author Ilia Vianni on 23.02.2019.
 */
public class FeedProcessingTask implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(FeedProcessingTask.class);
    private FeedConfiguration feedConfiguration;

    /**
     * Instantiates a new Feed Retriever.
     *
     * @param feedConfiguration the feed configuration
     */
    public FeedProcessingTask(FeedConfiguration feedConfiguration) {
        this.feedConfiguration = feedConfiguration;
    }

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

    private SyndFeed receiveFeed(FeedConfiguration feedConfiguration) throws FeedException, IOException {
        feedConfiguration.setLastRequestTime(Calendar.getInstance().getTime());
        SyndFeedInput input = new SyndFeedInput();
        return input.build(new XmlReader(feedConfiguration.getSourceUrl()));
    }

    private synchronized void saveContentToFile(File file, String str) throws IOException {
        if (!file.exists()) {
            file.createNewFile();
        }
        FileUtils.write(file, str, StandardCharsets.UTF_8, true);
    }
}
