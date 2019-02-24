package io.github.ivyanni.rssreader;

import io.github.ivyanni.rssreader.config.ApplicationConfiguration;
import io.github.ivyanni.rssreader.controller.ConsoleController;
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
        ConfigurationLoaderService configurationLoaderService = new ConfigurationLoaderServiceImpl();
        ApplicationConfiguration applicationConfiguration = configurationLoaderService.loadConfigurationFromFile();

        FeedService feedService = new FeedServiceImpl(applicationConfiguration);
        ConsoleController consoleController = new ConsoleController(applicationConfiguration, feedService);
        feedService.start();

        while(true) {
            System.out.print("Enter command: ");
            Scanner scanner = new Scanner(System.in);
            String command = scanner.next();
            switch (command) {
                case "add":
                    consoleController.createNewFeedDialog(scanner);
                    break;
                case "list":
                    consoleController.listExistingFeed();
                    break;
                case "remove":
                    consoleController.createRemoveFeedDialog(scanner);
                    break;
                case "exit":
                    feedService.stop();
                    configurationLoaderService.saveConfigurationToFile(applicationConfiguration);
                    System.out.println("Feeds processing was stopped");
                    scanner.close();
                    return;
            }
        }
    }
}
