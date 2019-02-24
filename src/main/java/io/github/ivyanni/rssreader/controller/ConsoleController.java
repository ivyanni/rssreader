package io.github.ivyanni.rssreader.controller;

import io.github.ivyanni.rssreader.config.ApplicationConfiguration;
import io.github.ivyanni.rssreader.config.FeedConfiguration;
import io.github.ivyanni.rssreader.constants.CLIConstants;
import io.github.ivyanni.rssreader.converters.RomeAttributesConverter;
import io.github.ivyanni.rssreader.service.FeedService;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.function.Predicate;

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
        String feedName = inputFeedName(scanner, true);
        URL feedUrl = inputFeedUrl(scanner);
        Long timeout = inputTimeout(scanner);
        String fileName = inputFilename(scanner);
        List<String> selectedParams = inputParameters(scanner);
        FeedConfiguration feedConfiguration = new FeedConfiguration();
        feedConfiguration.setFeedName(feedName);
        feedConfiguration.setFeedUrl(feedUrl);
        feedConfiguration.setTimeout(timeout);
        feedConfiguration.setFilename(fileName);
        feedConfiguration.setParams(selectedParams);
        feedService.addFeed(feedConfiguration);
        System.out.println(CLIConstants.FEED_ADDED_MESSAGE);
    }

    public void createRemoveFeedDialog(Scanner scanner) {
        String feedName = inputFeedName(scanner, false);
        feedService.removeFeed(feedName);
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

    public void showWelcomeMessage() {
        System.out.println("---------------------");
        System.out.println(CLIConstants.WELCOME_MESSAGE);
        System.out.println("---------------------");
        System.out.println(" ");
    }

    private String inputFilename(Scanner scanner) {
        String resultFilename = null;
        while (resultFilename == null) {
            System.out.print(CLIConstants.ENTER_FILENAME_MESSAGE);
            String fileName = scanner.nextLine();
            File file = new File(fileName);
            if (!file.canRead() && !file.canWrite() && !file.isFile()) {
                resultFilename = fileName;
            }
        }
        return resultFilename;
    }

    private Long inputTimeout(Scanner scanner) {
        Long resultTimeout = null;
        while (resultTimeout == null) {
            System.out.print(CLIConstants.ENTER_TIMEOUT_MESSAGE);
            String timeoutStr = scanner.nextLine();
            try {
                Long timeout = Long.parseLong(timeoutStr);
                if (timeout > 0) {
                    resultTimeout = timeout;
                } else {
                    System.out.println(CLIConstants.INCORRECT_NUMBER_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                System.out.println(CLIConstants.INCORRECT_NUMBER_MESSAGE);
            }
        }
        return resultTimeout;
    }

    private String inputFeedName(Scanner scanner, boolean unique) {
        boolean isUniqueName = false;
        String resultName = "";
        while (!isUniqueName) {
            System.out.print(CLIConstants.ENTER_FEED_NAME_MESSAGE);
            String feedName = scanner.nextLine();
            List<FeedConfiguration> feedConfigurationList = applicationConfiguration.getFeedConfigurationList();
            Predicate<FeedConfiguration> feedNamePredicate = config -> config.getFeedName().equalsIgnoreCase(feedName);
            isUniqueName = unique ?
                    feedConfigurationList.stream().noneMatch(feedNamePredicate) :
                    feedConfigurationList.stream().anyMatch(feedNamePredicate);
            if (!isUniqueName) {
                System.out.println(CLIConstants.INCORRECT_FEEDNAME_MESSAGE);
            } else resultName = feedName;
        }
        return resultName;
    }

    private List<String> inputParameters(Scanner scanner) {
        Set<String> allowedParams = RomeAttributesConverter.getAllowedParameters();
        System.out.println(CLIConstants.ALLOWED_PARAMETERS_MESSAGE + String.join(", ", allowedParams));
        System.out.print(CLIConstants.ENTER_PARAMETERS_MESSAGE);
        String paramLine = scanner.nextLine();
        if (paramLine.isEmpty()) {
            return List.copyOf(allowedParams);
        } else {
            paramLine = paramLine.toLowerCase();
            paramLine = paramLine.replaceAll("\\s*", "");
            return Arrays.asList(paramLine.split("\\s*,\\s*"));
        }
    }

    private URL inputFeedUrl(Scanner scanner) {
        URL feedUrl = null;
        while (feedUrl == null) {
            System.out.print(CLIConstants.ENTER_CORRECT_URL_MESSAGE);
            String enteredUrl = scanner.nextLine();
            try {
                feedUrl = new URL(enteredUrl);
            } catch (MalformedURLException ex) {
                System.out.println(CLIConstants.INCORRECT_URL_ENTERED_MESSAGE);
            }
        }
        return feedUrl;
    }
}
