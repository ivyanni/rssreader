package io.github.ivyanni.rssreader.service;

import io.github.ivyanni.rssreader.config.ApplicationConfiguration;

/**
 * @author Ilia Vianni on 23.02.2019.
 */
public interface ConfigurationLoaderService {

    ApplicationConfiguration loadConfigurationFromFile();

    void saveConfigurationToFile(ApplicationConfiguration applicationConfiguration);
}
