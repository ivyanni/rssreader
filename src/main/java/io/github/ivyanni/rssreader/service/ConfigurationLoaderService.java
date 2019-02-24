package io.github.ivyanni.rssreader.service;

import io.github.ivyanni.rssreader.config.ApplicationConfiguration;

/**
 * Service that manages application configuration.
 *
 * @author Ilia Vianni on 23.02.2019.
 */
public interface ConfigurationLoaderService {

    /**
     * Load existing configuration from file.
     *
     * @return the application configuration
     */
    ApplicationConfiguration loadConfigurationFromFile();

    /**
     * Save actual configuration to file.
     *
     * @param applicationConfiguration the application configuration
     */
    void saveConfigurationToFile(ApplicationConfiguration applicationConfiguration);
}
