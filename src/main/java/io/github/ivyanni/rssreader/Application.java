package io.github.ivyanni.rssreader;

import io.github.ivyanni.rssreader.config.ApplicationConfiguration;
import io.github.ivyanni.rssreader.constants.CLIConstants;
import io.github.ivyanni.rssreader.controller.ConsoleController;
import io.github.ivyanni.rssreader.service.ConfigurationLoaderService;
import io.github.ivyanni.rssreader.service.FeedUpdateService;
import io.github.ivyanni.rssreader.service.impl.ConfigurationLoaderServiceImpl;
import io.github.ivyanni.rssreader.service.impl.FeedUpdateServiceImpl;

import java.util.Scanner;

/**
 * @author Ilia Vianni on 23.02.2019.
 */
public class Application {

    public static void main(String[] args) {
        RomeAttributesHolder.fillAttributesMap();
        ConfigurationLoaderService configurationLoaderService = new ConfigurationLoaderServiceImpl("D:\\appconfig.json");
        ApplicationConfiguration applicationConfiguration = configurationLoaderService.loadConfigurationFromFile();

        FeedUpdateService feedUpdateService = new FeedUpdateServiceImpl(applicationConfiguration);
        feedUpdateService.startService();

        ConsoleController consoleController = new ConsoleController(applicationConfiguration, feedUpdateService);

        consoleController.showWelcomeMessage();

        while (true) {
            System.out.print(CLIConstants.ENTER_COMMAND_MESSAGE);
            Scanner scanner = new Scanner(System.in);
            String command = scanner.nextLine();
            command = command.toLowerCase();
            switch (command) {
                case CLIConstants.ADD_NEW_FEED_COMMAND:
                    consoleController.addNewFeed(scanner);
                    break;
                case CLIConstants.SHOW_EXISTING_FEEDS_COMMAND:
                    consoleController.listExistingFeed();
                    break;
                case CLIConstants.CHANGE_FEED_PARAMS_COMMAND:
                    consoleController.changeExistingFeed(scanner);
                    break;
                case CLIConstants.REMOVE_FEED_COMMAND:
                    consoleController.removeFeed(scanner);
                    break;
                case CLIConstants.EXIT_COMMAND:
                    consoleController.exit(scanner);
                    configurationLoaderService.saveConfigurationToFile(applicationConfiguration);
                    return;
            }
        }
    }
}
