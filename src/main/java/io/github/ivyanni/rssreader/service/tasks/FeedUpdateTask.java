package io.github.ivyanni.rssreader.service.tasks;

import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import io.github.ivyanni.rssreader.model.FeedConfiguration;
import io.github.ivyanni.rssreader.service.composers.FeedOutputComposer;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Performs feed update and saves feed entries to file.
 *
 * @author Ilia Vianni on 23.02.2019.
 */
public class FeedUpdateTask implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(FeedUpdateTask.class);
    private FeedOutputComposer feedOutputComposer = new FeedOutputComposer();
    private FeedConfiguration feedConfiguration;

    public FeedUpdateTask(FeedConfiguration feedConfiguration) {
        this.feedConfiguration = feedConfiguration;
    }

    /**
     * Receives feed by source URL using ROME and saves content to drive.
     */
    @Override
    public void run() {
        try {
            SyndFeed feed = new SyndFeedInput().build(new XmlReader(feedConfiguration.getSourceUrl()));
            List<String> content = feedOutputComposer.compose(feedConfiguration, feed);
            saveContent(content);
        } catch (FeedException ex) {
            LOGGER.error("Exception was occurred while parsing feed data: {}", ex.getMessage(), ex);
        } catch (UnknownHostException ex) {
            LOGGER.error("Check your internet connection. Connection to {} was failed", ex.getMessage(), ex);
        } catch (IOException ex) {
            LOGGER.error("Exception was occurred while retrieving feed data: {}", ex.getMessage(), ex);
        }
    }

    private void saveContent(List<String> content) throws IOException {
        if (content.size() > 0) {
            File file = new File(feedConfiguration.getOutputFilename());
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            for (String string : content) {
                saveStringToFile(file, string);
            }
        }
    }

    /**
     * Saves formatted entries string to specified file.
     *
     * @param file    File which will be used to save data
     * @param content Formatted feed entries string
     * @throws IOException when it couldn't append content to specified file
     */
    private synchronized void saveStringToFile(File file, String content) throws IOException {
        FileUtils.write(file, content, StandardCharsets.UTF_8, true);
    }
}
