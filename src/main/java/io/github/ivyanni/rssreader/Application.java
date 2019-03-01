package io.github.ivyanni.rssreader;

import io.github.ivyanni.rssreader.config.ApplicationConfiguration;
import io.github.ivyanni.rssreader.controller.CommandLineController;
import io.github.ivyanni.rssreader.service.ConfigurationLoaderService;
import io.github.ivyanni.rssreader.service.impl.ConfigurationLoaderServiceImpl;

/**
 * @author Ilia Vianni on 23.02.2019.
 */
public class Application {
    private static final String CONFIG_PATH = "config.json";

    public static void main(String[] args) {
        ConfigurationLoaderService configurationLoaderService = new ConfigurationLoaderServiceImpl(CONFIG_PATH);
        ApplicationConfiguration applicationConfiguration = configurationLoaderService.loadConfigurationFromFile();

        CommandLineController commandLineController =
                new CommandLineController(applicationConfiguration);

        commandLineController.startMainInteraction();

        configurationLoaderService.saveConfigurationToFile(applicationConfiguration);
    }
}
