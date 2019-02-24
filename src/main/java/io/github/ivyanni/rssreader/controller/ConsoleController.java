package io.github.ivyanni.rssreader.controller;

import io.github.ivyanni.rssreader.config.ApplicationConfiguration;
import io.github.ivyanni.rssreader.config.FeedConfiguration;
import io.github.ivyanni.rssreader.constants.CLIConstants;
import io.github.ivyanni.rssreader.service.FeedService;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * @author Ilia Vianni on 23.02.2019.
 */
public class ConsoleController {
    private ApplicationConfiguration applicationConfiguration;
    private FeedService feedService;

    public ConsoleController(ApplicationConfiguration applicationConfiguration, FeedService feedService) {
        this.applicationConfiguration = applicationConfiguration;
        this.feedService = feedService;
    }

    public void createNewFeedDialog(Scanner scanner) {
        URL feedUrl = inputFeedUrl(scanner);
        System.out.print(CLIConstants.ENTER_TIMEOUT_MESSAGE);
        Long timeout = scanner.nextLong();
        System.out.print(CLIConstants.ENTER_FILENAME_MESSAGE);
        String fileName = scanner.next();
        FeedConfiguration feedConfiguration = new FeedConfiguration();
        feedConfiguration.setFeedUrl(feedUrl);
        feedConfiguration.setTimeout(timeout);
        feedConfiguration.setFilename(fileName);
        feedService.addFeed(feedConfiguration);
        System.out.println(CLIConstants.FEED_ADDED_MESSAGE);
    }

    public void createRemoveFeedDialog(Scanner scanner) {
        URL feedUrl = inputFeedUrl(scanner);
        feedService.removeFeed(feedUrl);
        System.out.println(CLIConstants.FEED_REMOVED_MESSAGE);
    }

    public void listExistingFeed() {
        if (!applicationConfiguration.getFeedConfigurationList().isEmpty()) {
            System.out.println(CLIConstants.EXISTING_FEEDS_MESSAGE);
            applicationConfiguration.getFeedConfigurationList().forEach(feedConfiguration -> {
                System.out.println(feedConfiguration.getFeedUrl() + " / " + feedConfiguration.getTimeout());
            });
        } else {
            System.out.println(CLIConstants.NO_EXISTING_FEEDS_MESSAGE);
        }
    }

    private URL inputFeedUrl(Scanner scanner) {
        URL feedUrl = null;
        while (feedUrl == null) {
            System.out.print(CLIConstants.ENTER_CORRECT_URL_MESSAGE);
            String enteredUrl = scanner.next();
            try {
                feedUrl = new URL(enteredUrl);
            } catch (MalformedURLException ex) {
                System.out.println(CLIConstants.INCORRECT_URL_ENTERED_MESSAGE);
            }
        }
        return feedUrl;
    }
}
