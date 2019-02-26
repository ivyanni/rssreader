package io.github.ivyanni.rssreader.utils;

import com.rometools.rome.feed.synd.SyndContent;
import com.rometools.rome.feed.synd.SyndEntry;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * Contains mapping from ROME attribute name to function that returns string value for this attribute.
 *
 * @author Ilia Vianni on 24.02.2019.
 */
public class RomeAttributesMapper {
    private static final Map<String, Function<SyndEntry, String>> ATTR_TO_FUNCTION_MAP = new HashMap<>() {{
        put("title", SyndEntry::getTitle);
        put("author", SyndEntry::getAuthor);
        put("publishedDate", entry -> entry.getPublishedDate() != null ? entry.getPublishedDate().toString() : null);
        put("updatedDate", entry -> entry.getUpdatedDate() != null ? entry.getUpdatedDate().toString() : null);
        put("description", entry -> entry.getDescription() != null ? entry.getDescription().getValue() : null);
        put("content", entry -> entry.getContents().stream().map(SyndContent::getValue).reduce("", String::concat));
        put("uri", SyndEntry::getUri);
        put("link", SyndEntry::getLink);
        put("comments", SyndEntry::getComments);
    }};

    /**
     * Returns string value by feed's entry and specified attribute.
     *
     * @param entry     Feed's entry
     * @param attribute ROME attribute
     * @return the String value by defined feed's entry and attribute
     */
    public static synchronized String getValueByAttribute(SyndEntry entry, String attribute) {
        if (ATTR_TO_FUNCTION_MAP.containsKey(attribute)) {
            String value = ATTR_TO_FUNCTION_MAP.get(attribute).apply(entry);
            if (value != null) {
                return value;
            }
        }
        return "";
    }

    /**
     * Returns all attributes that available for user.
     *
     * @return available attributes
     */
    public static Set<String> getAvailableAttributes() {
        return ATTR_TO_FUNCTION_MAP.keySet();
    }
}
