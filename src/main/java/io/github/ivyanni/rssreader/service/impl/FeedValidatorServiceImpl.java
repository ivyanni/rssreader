package io.github.ivyanni.rssreader.service.impl;

import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import io.github.ivyanni.rssreader.model.FeedConfiguration;
import io.github.ivyanni.rssreader.service.FeedValidatorService;

import java.io.IOException;

/**
 * @author Ilia Vianni on 27.02.2019.
 */
public class FeedValidatorServiceImpl implements FeedValidatorService {

    @Override
    public boolean validateFeed(FeedConfiguration feedConfiguration) {
        try {
            SyndFeedInput input = new SyndFeedInput();
            SyndFeed feed = input.build(new XmlReader(feedConfiguration.getSourceUrl()));
            return !feed.getEntries().isEmpty() && feed.getEntries().stream()
                    .noneMatch(entry -> entry.getPublishedDate() == null);
        } catch (FeedException ex) {
            System.out.println("Error was occurred while parsing feed");
        } catch (IOException ex) {
            System.out.println("Error was occurred while reading feed");
        }
        return false;
    }
}
