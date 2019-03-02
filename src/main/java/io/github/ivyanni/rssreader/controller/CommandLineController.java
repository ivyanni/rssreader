package io.github.ivyanni.rssreader.controller;

import io.github.ivyanni.rssreader.model.ApplicationConfiguration;
import io.github.ivyanni.rssreader.model.FeedConfiguration;
import io.github.ivyanni.rssreader.model.constants.CLIConstants;
import io.github.ivyanni.rssreader.service.FeedUpdateSchedulerService;
import io.github.ivyanni.rssreader.service.FeedValidatorService;
import io.github.ivyanni.rssreader.service.impl.FeedUpdateSchedulerServiceImpl;
import io.github.ivyanni.rssreader.service.impl.FeedValidatorServiceImpl;
import io.github.ivyanni.rssreader.utils.CommandLineInputUtils;

import java.net.URL;
import java.util.Calendar;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

/**
 * Manages CLI and interactions with user.
 *
 * @author Ilia Vianni on 23.02.2019.
 */
public class CommandLineController {
    private ApplicationConfiguration applicationConfiguration;
    private FeedUpdateSchedulerService feedUpdateSchedulerService;
    private FeedValidatorService feedValidatorService;

    public CommandLineController(ApplicationConfiguration applicationConfiguration) {
        this.applicationConfiguration = applicationConfiguration;
        this.feedUpdateSchedulerService = new FeedUpdateSchedulerServiceImpl(applicationConfiguration);
        this.feedValidatorService = new FeedValidatorServiceImpl();
    }

    /**
     * Starts main interaction with user.
     */
    public void startMainInteraction() {
        feedUpdateSchedulerService.scheduleAllFeedUpdates();
        printWelcomeMessage();
        while (true) {
            System.out.print(CLIConstants.ENTER_COMMAND_MESSAGE);
            Scanner scanner = new Scanner(System.in);
            String command = scanner.nextLine();
            command = command.toLowerCase();
            switch (command) {
                case CLIConstants.ADD_NEW_FEED_COMMAND:
                    addNewFeed(scanner);
                    break;
                case CLIConstants.SHOW_EXISTING_FEEDS_COMMAND:
                    printExistingFeeds();
                    break;
                case CLIConstants.CHANGE_FEED_PARAMS_COMMAND:
                    changeExistingFeed(scanner);
                    break;
                case CLIConstants.REMOVE_FEED_COMMAND:
                    removeFeed(scanner);
                    break;
                case CLIConstants.EXIT_COMMAND:
                    feedUpdateSchedulerService.shutdownUpdates();
                    return;
            }
        }
    }

    /**
     * Manages interaction with user that leads to new feed subscription.
     *
     * @param scanner User's input Scanner
     */
    private void addNewFeed(Scanner scanner) {
        Set<String> existingNames = applicationConfiguration.getFeedConfigurations().keySet();
        String feedName = CommandLineInputUtils.inputFeedName(scanner, existingNames, true);
        URL feedUrl = CommandLineInputUtils.inputFeedUrl(scanner);
        Long delay = CommandLineInputUtils.inputNumber(scanner, CLIConstants.ENTER_DELAY_MESSAGE);
        Long chunkSize = CommandLineInputUtils.inputNumber(scanner, CLIConstants.ENTER_CHUNK_SIZE_MESSAGE);
        String fileName = CommandLineInputUtils.inputFilename(scanner, feedUrl);
        List<String> selectedParams = CommandLineInputUtils.inputParameters(scanner);

        FeedConfiguration feedConfiguration = new FeedConfiguration(feedUrl, fileName);
        feedConfiguration.setDelay(delay);
        feedConfiguration.setChunkSize(chunkSize);
        feedConfiguration.setOutputParams(selectedParams);

        boolean validationResult = feedValidatorService.validateFeed(feedConfiguration);
        if (validationResult) {
            feedConfiguration.setLastRequestTime(Calendar.getInstance().getTime());
            feedUpdateSchedulerService.scheduleFeedUpdate(feedConfiguration);
            applicationConfiguration.getFeedConfigurations().put(feedName, feedConfiguration);
            System.out.println(CLIConstants.FEED_ADDED_MESSAGE);
        } else {
            System.out.println(CLIConstants.INCORRECT_FEED_MESSAGE);
        }
    }

    /**
     * Manages interaction that leads to modifications in existing feed subscription.
     *
     * @param scanner User's input Scanner
     */
    private void changeExistingFeed(Scanner scanner) {
        Set<String> existingNames = applicationConfiguration.getFeedConfigurations().keySet();
        String feedName = CommandLineInputUtils.inputFeedName(scanner, existingNames, false);
        FeedConfiguration feedConfiguration = applicationConfiguration.getFeedConfigurations().get(feedName);
        System.out.print(CLIConstants.ENTER_COMMAND_MODIFY_MESSAGE);
        String command = scanner.nextLine();
        command = command.toLowerCase();

        switch (command) {
            case CLIConstants.CHANGE_URL_COMMAND:
                URL oldUrl = feedConfiguration.getSourceUrl();
                URL feedUrl = CommandLineInputUtils.inputFeedUrl(scanner);
                feedConfiguration.setSourceUrl(feedUrl);
                boolean validationResult = feedValidatorService.validateFeed(feedConfiguration);
                if (validationResult) {
                    feedConfiguration.setLastRequestTime(Calendar.getInstance().getTime());
                } else {
                    feedConfiguration.setSourceUrl(oldUrl);
                    System.out.println(CLIConstants.INCORRECT_FEED_MESSAGE);
                }
                break;
            case CLIConstants.CHANGE_DELAY_COMMAND:
                Long delay = CommandLineInputUtils.inputNumber(scanner, CLIConstants.ENTER_DELAY_MESSAGE);
                feedConfiguration.setDelay(delay);
                feedUpdateSchedulerService.rescheduleFeedUpdate(feedName, delay);
                break;
            case CLIConstants.CHANGE_CHUNK_SIZE_COMMAND:
                Long chunkSize = CommandLineInputUtils.inputNumber(scanner, CLIConstants.ENTER_CHUNK_SIZE_MESSAGE);
                feedConfiguration.setChunkSize(chunkSize);
                break;
            case CLIConstants.CHANGE_FILENAME_COMMAND:
                String filename = CommandLineInputUtils.inputFilename(scanner, feedConfiguration.getSourceUrl());
                feedConfiguration.setOutputFilename(filename);
                break;
            case CLIConstants.CHANGE_PARAMETERS_COMMAND:
                List<String> params = CommandLineInputUtils.inputParameters(scanner);
                feedConfiguration.setOutputParams(params);
                break;
        }
    }

    /**
     * Manages interaction that leads to feed removal.
     *
     * @param scanner User's input Scanner
     */
    private void removeFeed(Scanner scanner) {
        Set<String> existingNames = applicationConfiguration.getFeedConfigurations().keySet();
        String feedName = CommandLineInputUtils.inputFeedName(scanner, existingNames, false);
        feedUpdateSchedulerService.stopFeedUpdate(feedName);
        applicationConfiguration.getFeedConfigurations().remove(feedName);
        System.out.println(CLIConstants.FEED_REMOVED_MESSAGE);
    }

    /**
     * Prints existing feed list.
     */
    private void printExistingFeeds() {
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
     * Prints welcome message.
     */
    private void printWelcomeMessage() {
        System.out.println("---------------------");
        System.out.println(CLIConstants.WELCOME_MESSAGE);
        System.out.println("---------------------");
        System.out.println(" ");
    }
}
