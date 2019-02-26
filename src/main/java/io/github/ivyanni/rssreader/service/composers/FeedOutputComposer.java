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
 * Composes output string using received feed items and specified feed configuration.
 *
 * @author Ilia Vianni on 24.02.2019.
 */
public class FeedOutputComposer {

    /**
     * Composes string output.
     *
     * @param feedConfiguration Current feed's configuration
     * @param feed              Feed received by ROME
     * @return formatted output string
     */
    public String compose(FeedConfiguration feedConfiguration, SyndFeed feed) {
        List<SyndEntry> recentEntries = getRecentEntries(feedConfiguration, feed);
        recentEntries = getFirstEntries(recentEntries, feedConfiguration.getChunkSize());
        Date lastSavedMessageDate = recentEntries.size() > 0 ?
                recentEntries.get(recentEntries.size() - 1).getPublishedDate() :
                feedConfiguration.getLastMessageTime();
        feedConfiguration.setLastMessageTime(lastSavedMessageDate);
        return createStringByAttributes(recentEntries, feedConfiguration.getOutputParams());
    }

    /**
     * Filters out items that were already printed to file.
     *
     * @param feedConfiguration Current feed's configuration
     * @param feed              Feed received by ROME
     * @return sorted collection with new entries
     */
    private List<SyndEntry> getRecentEntries(FeedConfiguration feedConfiguration, SyndFeed feed) {
        return feed.getEntries().stream()
                .filter(entry -> feedConfiguration.getLastMessageTime() == null ||
                        entry.getPublishedDate().after(feedConfiguration.getLastMessageTime()))
                .sorted(Comparator.comparing(SyndEntry::getPublishedDate))
                .collect(Collectors.toList());
    }

    /**
     * Leaves only first chunkSize entries.
     *
     * @param entries   Collection of feed entries
     * @param chunkSize Chunk size specified in configuration
     * @return collection of entries with size <= specified chunk size
     */
    private List<SyndEntry> getFirstEntries(List<SyndEntry> entries, Long chunkSize) {
        return entries.size() > chunkSize ? entries.subList(0, chunkSize.intValue()) : entries;
    }

    /**
     * Creates string that contains specified attributes of feed entries
     *
     * @param entries    Collection of feed entries
     * @param attributes List of feed parameters specified in configuration
     * @return formatted string
     */
    private String createStringByAttributes(List<SyndEntry> entries, List<String> attributes) {
        StringBuilder outputSb = new StringBuilder();
        entries.forEach(entry -> {
            attributes.forEach(attr -> {
                outputSb.append(attr).append(": ")
                        .append(RomeAttributesMapper.getValueByAttribute(entry, attr))
                        .append(System.getProperty("line.separator"));
            });
        });
        return outputSb.toString();
    }
}
