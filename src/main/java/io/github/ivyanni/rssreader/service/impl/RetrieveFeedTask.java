package io.github.ivyanni.rssreader.service.impl;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import io.github.ivyanni.rssreader.config.FeedConfiguration;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Ilia Vianni on 23.02.2019.
 */
public class RetrieveFeedTask implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(RetrieveFeedTask.class);
    private FeedConfiguration feedConfiguration;

    RetrieveFeedTask(FeedConfiguration feedConfiguration) {
        this.feedConfiguration = feedConfiguration;
    }

    @Override
    public void run() {
        try {
            feedConfiguration.setLastRequestTime(Calendar.getInstance().getTime());
            SyndFeedInput input = new SyndFeedInput();
            SyndFeed feed = input.build(new XmlReader(feedConfiguration.getFeedUrl()));
            StringBuilder str = new StringBuilder();

            List<SyndEntry> selectedEntries = feed.getEntries().stream()
                    .filter(entry -> entry.getPublishedDate().after(feedConfiguration.getLastSavedMessageDate())
                            || feedConfiguration.getLastSavedMessageDate() == null)
                    .sorted(Comparator.comparing(SyndEntry::getPublishedDate))
                    .collect(Collectors.toList());
            Date lastSavedMessageDate = selectedEntries.get(selectedEntries.size() - 1).getPublishedDate();
            selectedEntries.forEach(entry -> {
                str.append(entry.getPublishedDate()).append(": ")
                        .append(entry.getTitle()).append(System.getProperty("line.separator"));
            });

            if (str.length() > 0) {
                File file = new File(feedConfiguration.getFilename());
                saveToFile(file, str.toString());
            }
            feedConfiguration.setLastSavedMessageDate(lastSavedMessageDate);
        } catch (UnknownHostException ex) {
            LOGGER.error("Check your internet connection. Connection to {} was failed", ex.getMessage(), ex);
        } catch (FeedException | IOException ex) {
            LOGGER.error("Exception was occurred while retrieving feed data: {}", ex.getMessage(), ex);
        }
    }

    private synchronized void saveToFile(File file, String str) {
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            FileUtils.write(file, str, StandardCharsets.UTF_8, true);
        } catch (IOException ex) {
            LOGGER.error("Exception was occurred while saving feed to file: " + ex.getMessage(), ex);
        }
    }
}
