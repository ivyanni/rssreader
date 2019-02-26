package io.github.ivyanni.rssreader.service.composers;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import io.github.ivyanni.rssreader.RomeAttributesHolder;
import io.github.ivyanni.rssreader.config.FeedConfiguration;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Class that composes output string using Feed and specified configuration.
 *
 * @author Ilia Vianni on 24.02.2019.
 */
public class FeedOutputComposer {

    /**
     * Compose string output using feed and actual configuration.
     *
     * @param feedConfiguration the feed configuration
     * @param feed              the feed
     * @return output string
     */
    public String compose(FeedConfiguration feedConfiguration, SyndFeed feed) {
        List<SyndEntry> actualEntries = getActualEntries(feedConfiguration, feed);
        actualEntries = getFirstEntries(actualEntries, feedConfiguration.getItemsAmount());
        Date lastSavedMessageDate = actualEntries.get(actualEntries.size() - 1).getPublishedDate();
        feedConfiguration.setLastSavedMessageDate(lastSavedMessageDate);
        return createStringByAttributes(actualEntries, feedConfiguration.getParams());
    }

    private List<SyndEntry> getActualEntries(FeedConfiguration feedConfiguration, SyndFeed feed) {
        return feed.getEntries().stream()
                .filter(entry -> feedConfiguration.getLastSavedMessageDate() == null ||
                        entry.getPublishedDate().after(feedConfiguration.getLastSavedMessageDate()))
                .sorted(Comparator.comparing(SyndEntry::getPublishedDate))
                .collect(Collectors.toList());
    }

    private List<SyndEntry> getFirstEntries(List<SyndEntry> entries, Long amount) {
        if (entries.size() > amount) {
            entries = entries.subList(0, amount.intValue());
        }
        return entries;
    }

    private String createStringByAttributes(List<SyndEntry> selectedEntries, List<String> attributes) {
        StringBuilder str = new StringBuilder();
        selectedEntries.forEach(entry -> {
            attributes.forEach(attr -> {
                str.append(attr).append(": ")
                        .append(RomeAttributesHolder.getValueByAttribute(entry, attr))
                        .append(System.getProperty("line.separator"));
            });
            str.append("-----").append(System.getProperty("line.separator"));
        });
        return str.toString();
    }
}
