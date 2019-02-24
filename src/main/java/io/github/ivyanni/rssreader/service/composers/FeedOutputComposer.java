package io.github.ivyanni.rssreader.service.composers;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import io.github.ivyanni.rssreader.config.FeedConfiguration;
import io.github.ivyanni.rssreader.converters.RomeAttributesConverter;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Ilia Vianni on 24.02.2019.
 */
public class FeedOutputComposer {

    public String compose(FeedConfiguration feedConfiguration, SyndFeed feed) {
        List<SyndEntry> selectedEntries = feed.getEntries().stream()
                .filter(entry -> feedConfiguration.getLastSavedMessageDate() == null ||
                        entry.getPublishedDate().after(feedConfiguration.getLastSavedMessageDate()))
                .sorted(Comparator.comparing(SyndEntry::getPublishedDate))
                .collect(Collectors.toList());

        Date lastSavedMessageDate = selectedEntries.get(selectedEntries.size() - 1).getPublishedDate();
        feedConfiguration.setLastSavedMessageDate(lastSavedMessageDate);

        return createStringByAttributes(selectedEntries, feedConfiguration.getParams());
    }

    private String createStringByAttributes(List<SyndEntry> selectedEntries, List<String> attributes) {
        StringBuilder str = new StringBuilder();
        selectedEntries.forEach(entry -> {
            attributes.forEach(attr -> {
                str.append(attr).append(": ")
                        .append(RomeAttributesConverter.getValueByAttribute(entry, attr))
                        .append(System.getProperty("line.separator"));
            });
            str.append("-----").append(System.getProperty("line.separator"));
        });
        return str.toString();
    }
}
