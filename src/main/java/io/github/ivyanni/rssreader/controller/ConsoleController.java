package io.github.ivyanni.rssreader.controller;

import io.github.ivyanni.rssreader.config.ApplicationConfiguration;
import io.github.ivyanni.rssreader.config.FeedConfiguration;
import io.github.ivyanni.rssreader.constants.CLIConstants;
import io.github.ivyanni.rssreader.service.FeedUpdaterService;
import io.github.ivyanni.rssreader.utils.ConsoleInputUtils;

import java.net.URL;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

/**
 * Controller that manages CLI and interaction with user.
 *
 * @author Ilia Vianni on 23.02.2019.
 */
public class ConsoleController {
    private ApplicationConfiguration applicationConfiguration;
    private FeedUpdaterService feedUpdaterService;

    /**
     * Instantiates a new Console controller.
     *
     * @param applicationConfiguration the application configuration
     * @param feedUpdaterService       the feed update service
     */
    public ConsoleController(ApplicationConfiguration applicationConfiguration,
                             FeedUpdaterService feedUpdaterService) {
        this.applicationConfiguration = applicationConfiguration;
        this.feedUpdaterService = feedUpdaterService;
    }

    /**
     * Starts dialog with user that leads to new feed creation.
     *
     * @param scanner the scanner
     */
    public void addNewFeed(Scanner scanner) {
        Set<String> existingNames = applicationConfiguration.getFeedConfigurations().keySet();
        String feedName = ConsoleInputUtils.inputFeedName(scanner, existingNames, true);
        URL feedUrl = ConsoleInputUtils.inputFeedUrl(scanner);
        Long delay = ConsoleInputUtils.inputNumber(scanner, CLIConstants.ENTER_DELAY_MESSAGE);
        Long chunkSize = ConsoleInputUtils.inputNumber(scanner, CLIConstants.ENTER_CHUNK_SIZE_MESSAGE);
        String fileName = ConsoleInputUtils.inputFilename(scanner);
        List<String> selectedParams = ConsoleInputUtils.inputParameters(scanner);
        FeedConfiguration feedConfiguration = new FeedConfiguration(feedUrl, fileName);
        feedConfiguration.setDelay(delay);
        feedConfiguration.setChunkSize(chunkSize);
        feedConfiguration.setOutputParams(selectedParams);
        feedUpdaterService.scheduleFeedUpdate(feedConfiguration);
        applicationConfiguration.getFeedConfigurations().put(feedName, feedConfiguration);
        System.out.println(CLIConstants.FEED_ADDED_MESSAGE);
    }

    /**
     * Starts dialog that leads to modifications in existing feed.
     *
     * @param scanner the scanner
     */
    public void changeExistingFeed(Scanner scanner) {
        Set<String> existingNames = applicationConfiguration.getFeedConfigurations().keySet();
        String feedName = ConsoleInputUtils.inputFeedName(scanner, existingNames, false);
        FeedConfiguration feedConfiguration = applicationConfiguration.getFeedConfigurations().get(feedName);
        System.out.print(CLIConstants.ENTER_COMMAND_MODIFY_MESSAGE);
        String command = scanner.nextLine();
        command = command.toLowerCase();
        switch (command) {
            case CLIConstants.CHANGE_URL_COMMAND:
                URL feedUrl = ConsoleInputUtils.inputFeedUrl(scanner);
                feedConfiguration.setSourceUrl(feedUrl);
                feedConfiguration.setLastMessageTime(null);
                break;
            case CLIConstants.CHANGE_DELAY_COMMAND:
                Long delay = ConsoleInputUtils.inputNumber(scanner, CLIConstants.ENTER_DELAY_MESSAGE);
                feedConfiguration.setDelay(delay);
                feedUpdaterService.rescheduleFeedUpdate(feedName, delay);
                break;
            case CLIConstants.CHANGE_CHUNK_SIZE_COMMAND:
                Long chunkSize = ConsoleInputUtils.inputNumber(scanner, CLIConstants.ENTER_CHUNK_SIZE_MESSAGE);
                feedConfiguration.setChunkSize(chunkSize);
                break;
            case CLIConstants.CHANGE_FILENAME_COMMAND:
                String filename = ConsoleInputUtils.inputFilename(scanner);
                feedConfiguration.setOutputFilename(filename);
                break;
            case CLIConstants.CHANGE_PARAMETERS_COMMAND:
                List<String> params = ConsoleInputUtils.inputParameters(scanner);
                feedConfiguration.setOutputParams(params);
                break;
        }
    }

    /**
     * Starts dialog that results to feed removal.
     *
     * @param scanner the scanner
     */
    public void removeFeed(Scanner scanner) {
        Set<String> existingNames = applicationConfiguration.getFeedConfigurations().keySet();
        String feedName = ConsoleInputUtils.inputFeedName(scanner, existingNames, false);
        feedUpdaterService.stopFeedUpdate(feedName);
        applicationConfiguration.getFeedConfigurations().remove(feedName);
        System.out.println(CLIConstants.FEED_REMOVED_MESSAGE);
    }

    /**
     * Shutdowns existing services.
     */
    public void exit() {
        feedUpdaterService.stopService();
    }

    /**
     * Shows existing feeds.
     */
    public void listExistingFeed() {
        if (!applicationConfiguration.getFeedConfigurations().isEmpty()) {
            System.out.println(CLIConstants.EXISTING_FEEDS_MESSAGE);
            StringBuilder existingFeedsSb = new StringBuilder();
            applicationConfiguration.getFeedConfigurations().forEach((name, config) -> {
                existingFeedsSb.append(name).append(" | ").append(config.getSourceUrl())
                        .append(" | ").append(config.getDelay())
                        .append(" | ").append(config.getOutputFilename())
                        .append(System.getProperty("line.separator"));
            });
            System.out.print(existingFeedsSb.toString());
        } else {
            System.out.println(CLIConstants.NO_EXISTING_FEEDS_MESSAGE);
        }
    }

    /**
     * Shows welcome message.
     */
    public void showWelcomeMessage() {
        System.out.println("---------------------");
        System.out.println(CLIConstants.WELCOME_MESSAGE);
        System.out.println("---------------------");
        System.out.println(" ");
    }
}
