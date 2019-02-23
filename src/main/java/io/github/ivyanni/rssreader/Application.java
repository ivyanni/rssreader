package io.github.ivyanni.rssreader;

import io.github.ivyanni.rssreader.config.ApplicationConfiguration;
import io.github.ivyanni.rssreader.config.FeedConfiguration;
import io.github.ivyanni.rssreader.service.ConfigurationLoaderService;
import io.github.ivyanni.rssreader.service.FeedService;
import io.github.ivyanni.rssreader.service.impl.ConfigurationLoaderServiceImpl;
import io.github.ivyanni.rssreader.service.impl.FeedServiceImpl;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * @author Ilia Vianni on 23.02.2019.
 */
public class Application {

    public static void main(String[] args) {
        FeedService feedService = new FeedServiceImpl();
        ConfigurationLoaderService configurationLoaderService = new ConfigurationLoaderServiceImpl();
        ApplicationConfiguration applicationConfiguration = configurationLoaderService.loadConfigurationFromFile();

        feedService.start(applicationConfiguration);

        while(true) {
            System.out.print("Enter command: ");
            Scanner scanner = new Scanner(System.in);
            String command = scanner.next();
            switch (command) {
                case "add":
                    System.out.print("Enter URL: ");
                    String feedUrl = scanner.next();
                    try {
                        FeedConfiguration feedConfiguration = new FeedConfiguration();
                        feedConfiguration.setFeedUrl(new URL(feedUrl));
                        feedConfiguration.setTimeout(2L);
                        feedConfiguration.setFilename("D:\\test.txt");
                        feedService.addFeed(applicationConfiguration, feedConfiguration);
                        System.out.println("Feed was added");
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                    break;
                case "stop":
                    feedService.stop(applicationConfiguration);
                    configurationLoaderService.saveConfigurationToFile(applicationConfiguration);
                    System.out.println("Feeds processing was stopped");
                    break;
                case "list":
                    System.out.println("Existing feeds:");
                    applicationConfiguration.getFeedConfigurationList().forEach(feedConfiguration -> {
                        System.out.println(feedConfiguration.getFeedUrl());
                    });
                    break;
                case "remove":
                    System.out.print("Enter URL: ");
                    String feedUrlToRemove = scanner.next();
                    try {
                        feedService.removeFeed(applicationConfiguration, new URL(feedUrlToRemove));
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                    System.out.println("Feed " + feedUrlToRemove + " was removed");
                    break;
                case "exit":
                    feedService.stop(applicationConfiguration);
                    configurationLoaderService.saveConfigurationToFile(applicationConfiguration);
                    System.out.println("Feeds processing was stopped");
                    return;
            }
        }
    }
}
