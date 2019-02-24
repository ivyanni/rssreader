package io.github.ivyanni.rssreader.service.tasks;

import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import io.github.ivyanni.rssreader.config.FeedConfiguration;
import io.github.ivyanni.rssreader.service.composers.FeedOutputComposer;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;

/**
 * @author Ilia Vianni on 23.02.2019.
 */
public class RetrieveFeedTask implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(RetrieveFeedTask.class);
    private FeedConfiguration feedConfiguration;
    private FeedOutputComposer feedOutputComposer;

    public RetrieveFeedTask(FeedConfiguration feedConfiguration) {
        this.feedConfiguration = feedConfiguration;
        this.feedOutputComposer = new FeedOutputComposer();
    }

    @Override
    public void run() {
        try {
            SyndFeed feed = retrieveFeed(feedConfiguration);
            String outputString = feedOutputComposer.compose(feedConfiguration, feed);
            if (outputString.length() > 0) {
                File file = new File(feedConfiguration.getFilename());
                saveToFile(file, outputString);
            }
        } catch (UnknownHostException ex) {
            LOGGER.error("Check your internet connection. Connection to {} was failed", ex.getMessage(), ex);
        } catch (FeedException | IOException ex) {
            LOGGER.error("Exception was occurred while retrieving feed data: {}", ex.getMessage(), ex);
        }
    }

    private SyndFeed retrieveFeed(FeedConfiguration feedConfiguration) throws FeedException, IOException {
        feedConfiguration.setLastRequestTime(Calendar.getInstance().getTime());
        SyndFeedInput input = new SyndFeedInput();
        return input.build(new XmlReader(feedConfiguration.getFeedUrl()));
    }

    private synchronized void saveToFile(File file, String str) throws IOException {
        if (!file.exists()) {
            file.createNewFile();
        }
        FileUtils.write(file, str, StandardCharsets.UTF_8, true);
    }
}
