package io.github.ivyanni.rssreader.service;

import io.github.ivyanni.rssreader.config.ApplicationConfiguration;

/**
 * Manages operations with application configuration.
 * @author Ilia Vianni on 23.02.2019.
 */
public interface ConfigurationLoaderService {

    /**
     * Loads existing configuration from file.
     * @return application's configuration object
     */
    ApplicationConfiguration loadConfigurationFromFile();

    /**
     * Saves actual configuration to file.
     * @param applicationConfiguration application's configuration object
     */
    void saveConfigurationToFile(ApplicationConfiguration applicationConfiguration);
}
