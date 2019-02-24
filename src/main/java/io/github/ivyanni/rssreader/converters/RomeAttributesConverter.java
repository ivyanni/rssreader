package io.github.ivyanni.rssreader.converters;

import com.rometools.rome.feed.synd.SyndContent;
import com.rometools.rome.feed.synd.SyndEntry;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * @author Ilia Vianni on 24.02.2019.
 */
public class RomeAttributesConverter {
    private static Map<String, Function<SyndEntry, String>> attributeToFunctionMap = new HashMap<>();

    public static void fillAttributesMap() {
        attributeToFunctionMap.put("title", SyndEntry::getTitle);
        attributeToFunctionMap.put("author", SyndEntry::getAuthor);
        attributeToFunctionMap.put("publishedDate", entry -> entry.getPublishedDate().toString());
        attributeToFunctionMap.put("updatedDate", entry -> entry.getUpdatedDate().toString());
        attributeToFunctionMap.put("titleEx", entry -> entry.getTitleEx().getValue());
        attributeToFunctionMap.put("description", entry -> entry.getDescription().getValue());
        attributeToFunctionMap.put("content", entry -> entry.getContents().stream().map(SyndContent::getValue).reduce("", String::concat));
        attributeToFunctionMap.put("uri", SyndEntry::getUri);
        attributeToFunctionMap.put("link", SyndEntry::getLink);
        attributeToFunctionMap.put("comments", SyndEntry::getComments);
    }

    public static String getValueByAttribute(SyndEntry entry, String attribute) {
        if (attributeToFunctionMap.containsKey(attribute)) {
            return attributeToFunctionMap.get(attribute).apply(entry);
        } else return "";
    }
}
