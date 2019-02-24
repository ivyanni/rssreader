package io.github.ivyanni.rssreader.service.composers;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import io.github.ivyanni.rssreader.config.FeedConfiguration;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Ilia Vianni on 24.02.2019.
 */
public class FeedOutputComposer {

    public String compose(FeedConfiguration feedConfiguration, SyndFeed feed) {
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

        feedConfiguration.setLastSavedMessageDate(lastSavedMessageDate);
        return str.toString();
    }
}
