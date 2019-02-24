package io.github.ivyanni.rssreader;

import io.github.ivyanni.rssreader.config.ApplicationConfiguration;
import io.github.ivyanni.rssreader.constants.CLIConstants;
import io.github.ivyanni.rssreader.controller.ConsoleController;
import io.github.ivyanni.rssreader.converters.RomeAttributesConverter;
import io.github.ivyanni.rssreader.service.ConfigurationLoaderService;
import io.github.ivyanni.rssreader.service.FeedService;
import io.github.ivyanni.rssreader.service.impl.ConfigurationLoaderServiceImpl;
import io.github.ivyanni.rssreader.service.impl.FeedServiceImpl;

import java.util.Scanner;

/**
 * @author Ilia Vianni on 23.02.2019.
 */
public class Application {

    public static void main(String[] args) {
        RomeAttributesConverter.fillAttributesMap();
        ConfigurationLoaderService configurationLoaderService = new ConfigurationLoaderServiceImpl();
        ApplicationConfiguration applicationConfiguration = configurationLoaderService.loadConfigurationFromFile();

        FeedService feedService = new FeedServiceImpl(applicationConfiguration);
        ConsoleController consoleController = new ConsoleController(applicationConfiguration, feedService);
        feedService.start();


        while(true) {
            System.out.print(CLIConstants.ENTER_COMMAND_MESSAGE);
            Scanner scanner = new Scanner(System.in);
            String command = scanner.nextLine();
            command = command.toLowerCase();
            switch (command) {
                case CLIConstants.ADD_NEW_FEED_COMMAND:
                    consoleController.createNewFeedDialog(scanner);
                    break;
                case CLIConstants.SHOW_EXISTING_FEEDS_COMMAND:
                    consoleController.listExistingFeed();
                    break;
                case CLIConstants.REMOVE_FEED_COMMAND:
                    consoleController.createRemoveFeedDialog(scanner);
                    break;
                case CLIConstants.EXIT_COMMAND:
                    feedService.stop();
                    configurationLoaderService.saveConfigurationToFile(applicationConfiguration);
                    System.out.println("Feeds processing was stopped");
                    scanner.close();
                    return;
            }
        }
    }
}
