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
        List<SyndEntry> selectedEntries = feed.getEntries().stream()
                .filter(entry -> feedConfiguration.getLastSavedMessageDate() == null ||
                        entry.getPublishedDate().after(feedConfiguration.getLastSavedMessageDate()))
                .sorted(Comparator.comparing(SyndEntry::getPublishedDate))
                .collect(Collectors.toList());

        if(selectedEntries.size() > feedConfiguration.getItemsAmount()) {
            selectedEntries = selectedEntries.subList(0, feedConfiguration.getItemsAmount().intValue());
        }

        Date lastSavedMessageDate = selectedEntries.get(selectedEntries.size() - 1).getPublishedDate();
        feedConfiguration.setLastSavedMessageDate(lastSavedMessageDate);

        return createStringByAttributes(selectedEntries, feedConfiguration.getParams());
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
