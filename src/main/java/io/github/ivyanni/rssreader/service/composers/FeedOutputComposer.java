package io.github.ivyanni.rssreader.service.composers;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import io.github.ivyanni.rssreader.model.FeedConfiguration;
import io.github.ivyanni.rssreader.utils.RomeAttributesMapper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Composes a collection of output strings.
 *
 * @author Ilia Vianni on 24.02.2019.
 */
public class FeedOutputComposer {

    /**
     * Composes a collection of RSS items in string format.
     *
     * @param feedConfiguration Current feed's configuration
     * @param feed              Feed received by ROME
     * @return collection of formatted RSS items
     */
    public List<String> compose(FeedConfiguration feedConfiguration, SyndFeed feed) {
        List<SyndEntry> recentEntries = getRecentEntries(feedConfiguration, feed);
        recentEntries = getLastEntries(recentEntries, feedConfiguration.getChunkSize());
        feedConfiguration.setLastRequestTime(Calendar.getInstance().getTime());
        return createStringListByAttributes(recentEntries, feedConfiguration.getOutputParams());
    }

    /**
     * Filters out items that were published before previous feed update.
     *
     * @param feedConfiguration Current feed's configuration
     * @param feed              Feed received by ROME
     * @return sorted collection with new entries
     */
    private List<SyndEntry> getRecentEntries(FeedConfiguration feedConfiguration, SyndFeed feed) {
        return feed.getEntries().stream()
                .filter(entry -> feedConfiguration.getLastRequestTime() != null &&
                        entry.getPublishedDate().after(feedConfiguration.getLastRequestTime()))
                .sorted(Comparator.comparing(SyndEntry::getPublishedDate))
                .collect(Collectors.toList());
    }

    /**
     * Leaves only last chunkSize entries.
     *
     * @param entries   Collection of feed entries
     * @param chunkSize Chunk size specified in configuration
     * @return collection of last entries
     */
    private List<SyndEntry> getLastEntries(List<SyndEntry> entries, Long chunkSize) {
        return entries.size() > chunkSize ?
                entries.subList(entries.size() - chunkSize.intValue() - 1, entries.size() - 1) : entries;
    }

    /**
     * Creates strings collection where each contains user-specified attributes
     *
     * @param entries    Collection of feed entries
     * @param attributes List of feed parameters specified in configuration
     * @return formatted strings collection
     */
    private List<String> createStringListByAttributes(List<SyndEntry> entries, List<String> attributes) {
        List<String> stringList = new ArrayList<>();
        StringBuilder outputSb = new StringBuilder();
        entries.forEach(entry -> {
            attributes.forEach(attr -> {
                outputSb.append(attr).append(": ")
                        .append(RomeAttributesMapper.getValueByAttribute(entry, attr))
                        .append(System.getProperty("line.separator"));
            });
            stringList.add(outputSb.toString());
            outputSb.setLength(0);
        });
        return stringList;
    }
}
