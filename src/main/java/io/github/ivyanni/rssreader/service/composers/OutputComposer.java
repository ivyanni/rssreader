package io.github.ivyanni.rssreader.service.composers;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import io.github.ivyanni.rssreader.config.FeedConfiguration;
import io.github.ivyanni.rssreader.utils.RomeAttributesMapper;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Class that composes output string using Feed and specified configuration.
 *
 * @author Ilia Vianni on 24.02.2019.
 */
public class OutputComposer {

    /**
     * Compose string output using feed and actual configuration.
     *
     * @param feedConfiguration the feed configuration
     * @param feed              the feed
     * @return output string
     */
    public String compose(FeedConfiguration feedConfiguration, SyndFeed feed) {
        List<SyndEntry> actualEntries = getActualEntries(feedConfiguration, feed);
        actualEntries = getFirstEntries(actualEntries, feedConfiguration.getChunkSize());
        Date lastSavedMessageDate = actualEntries.size() > 0 ?
                actualEntries.get(actualEntries.size() - 1).getPublishedDate() :
                feedConfiguration.getLastMessageTime();
        feedConfiguration.setLastMessageTime(lastSavedMessageDate);
        return createStringByAttributes(actualEntries, feedConfiguration.getOutputParams());
    }

    private List<SyndEntry> getActualEntries(FeedConfiguration feedConfiguration, SyndFeed feed) {
        return feed.getEntries().stream()
                .filter(entry -> feedConfiguration.getLastMessageTime() == null ||
                        entry.getPublishedDate().after(feedConfiguration.getLastMessageTime()))
                .sorted(Comparator.comparing(SyndEntry::getPublishedDate))
                .collect(Collectors.toList());
    }

    private List<SyndEntry> getFirstEntries(List<SyndEntry> entries, Long chunkSize) {
        return entries.size() > chunkSize ? entries.subList(0, chunkSize.intValue()) : entries;
    }

    private String createStringByAttributes(List<SyndEntry> selectedEntries, List<String> attributes) {
        StringBuilder outputSb = new StringBuilder();
        selectedEntries.forEach(entry -> {
            attributes.forEach(attr -> {
                outputSb.append(attr).append(": ")
                        .append(RomeAttributesMapper.getValueByAttribute(entry, attr))
                        .append(System.getProperty("line.separator"));
            });
        });
        return outputSb.toString();
    }
}
