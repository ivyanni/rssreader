package io.github.ivyanni.rssreader.controller;

import io.github.ivyanni.rssreader.config.ApplicationConfiguration;
import io.github.ivyanni.rssreader.config.FeedConfiguration;
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
        System.out.print("Enter URL: ");
        String feedUrl = scanner.next();
        System.out.print("Enter timeout (sec): ");
        Long timeout = scanner.nextLong();
        System.out.print("Enter filename: ");
        String fileName = scanner.next();
        try {
            FeedConfiguration feedConfiguration = new FeedConfiguration();
            feedConfiguration.setFeedUrl(new URL(feedUrl));
            feedConfiguration.setTimeout(timeout);
            feedConfiguration.setFilename(fileName);
            feedService.addFeed(feedConfiguration);
            System.out.println("Feed was added");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public void createRemoveFeedDialog(Scanner scanner) {
        System.out.print("Enter URL: ");
        String feedUrlToRemove = scanner.next();
        try {
            feedService.removeFeed(new URL(feedUrlToRemove));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        System.out.println("Feed " + feedUrlToRemove + " was removed");
    }

    public void listExistingFeed() {
        if(!applicationConfiguration.getFeedConfigurationList().isEmpty()) {
            System.out.println("Existing feeds:");
            applicationConfiguration.getFeedConfigurationList().forEach(feedConfiguration -> {
                System.out.println(feedConfiguration.getFeedUrl() + " / " + feedConfiguration.getTimeout());
            });
        } else {
            System.out.println("No existing feeds");
        }
    }
}
