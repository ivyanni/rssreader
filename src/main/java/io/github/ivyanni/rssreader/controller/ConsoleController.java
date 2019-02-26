package io.github.ivyanni.rssreader.controller;

import io.github.ivyanni.rssreader.config.ApplicationConfiguration;
import io.github.ivyanni.rssreader.config.FeedConfiguration;
import io.github.ivyanni.rssreader.constants.CLIConstants;
import io.github.ivyanni.rssreader.service.FeedUpdateService;
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
    private FeedUpdateService feedUpdateService;

    /**
     * Instantiates a new Console controller.
     *
     * @param applicationConfiguration the application configuration
     * @param feedUpdateService        the feed update service
     */
    public ConsoleController(ApplicationConfiguration applicationConfiguration, FeedUpdateService feedUpdateService) {
        this.applicationConfiguration = applicationConfiguration;
        this.feedUpdateService = feedUpdateService;
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
        Long timeout = ConsoleInputUtils.inputNumber(scanner, CLIConstants.ENTER_TIMEOUT_MESSAGE);
        Long amount = ConsoleInputUtils.inputNumber(scanner, CLIConstants.ENTER_AMOUNT_MESSAGE);
        String fileName = ConsoleInputUtils.inputFilename(scanner);
        List<String> selectedParams = ConsoleInputUtils.inputParameters(scanner);
        FeedConfiguration feedConfiguration = new FeedConfiguration();
        feedConfiguration.setFeedUrl(feedUrl);
        feedConfiguration.setTimeout(timeout);
        feedConfiguration.setFilename(fileName);
        feedConfiguration.setItemsAmount(amount);
        feedConfiguration.setParams(selectedParams);
        feedUpdateService.scheduleFeedUpdate(feedName, feedConfiguration);
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
        switch (command) {
            case CLIConstants.CHANGE_URL_COMMAND:
                URL feedUrl = ConsoleInputUtils.inputFeedUrl(scanner);
                feedConfiguration.setFeedUrl(feedUrl);
                feedConfiguration.setLastSavedMessageDate(null);
                break;
            case CLIConstants.CHANGE_TIMEOUT_COMMAND:
                Long timeout = ConsoleInputUtils.inputNumber(scanner, CLIConstants.ENTER_TIMEOUT_MESSAGE);
                feedConfiguration.setTimeout(timeout);
                feedUpdateService.rescheduleFeedUpdate(feedName, timeout);
                break;
            case CLIConstants.CHANGE_AMOUNT_COMMAND:
                Long amount = ConsoleInputUtils.inputNumber(scanner, CLIConstants.ENTER_AMOUNT_MESSAGE);
                feedConfiguration.setItemsAmount(amount);
                break;
            case CLIConstants.CHANGE_FILENAME_COMMAND:
                String filename = ConsoleInputUtils.inputFilename(scanner);
                feedConfiguration.setFilename(filename);
                break;
            case CLIConstants.CHANGE_PARAMETERS_COMMAND:
                List<String> params = ConsoleInputUtils.inputParameters(scanner);
                feedConfiguration.setParams(params);
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
        feedUpdateService.stopFeedUpdate(feedName);
        applicationConfiguration.getFeedConfigurations().remove(feedName);
        System.out.println(CLIConstants.FEED_REMOVED_MESSAGE);
    }

    /**
     * Shutdowns existing services.
     *
     * @param scanner the scanner
     */
    public void exit(Scanner scanner) {
        feedUpdateService.stopService();
        scanner.close();
    }

    /**
     * Shows existing feeds.
     */
    public void listExistingFeed() {
        if (!applicationConfiguration.getFeedConfigurations().isEmpty()) {
            System.out.println(CLIConstants.EXISTING_FEEDS_MESSAGE);
            applicationConfiguration.getFeedConfigurations().forEach((name, config) -> {
                System.out.println(name + " / " + config.getFeedUrl() + " / " + config.getFilename());
            });
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
