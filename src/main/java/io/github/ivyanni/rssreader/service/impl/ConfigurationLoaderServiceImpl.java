package io.github.ivyanni.rssreader.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.github.ivyanni.rssreader.config.ApplicationConfiguration;
import io.github.ivyanni.rssreader.service.ConfigurationLoaderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

/**
 * @author Ilia Vianni on 23.02.2019.
 */
public class ConfigurationLoaderServiceImpl implements ConfigurationLoaderService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationLoaderServiceImpl.class);
    private ObjectMapper objectMapper;
    private String configFileName;

    public ConfigurationLoaderServiceImpl(String configFileName) {
        this.configFileName = configFileName;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    @Override
    public ApplicationConfiguration loadConfigurationFromFile() {
        ApplicationConfiguration applicationConfiguration = new ApplicationConfiguration();
        File appConfigFile = new File(configFileName);
        try {
            if (appConfigFile.exists()) {
                applicationConfiguration = objectMapper.readValue(appConfigFile, ApplicationConfiguration.class);
            } else {
                appConfigFile.createNewFile();
            }
        } catch (IOException ex) {
            LOGGER.warn("Exception was occurred while retrieving configuration file: {}", ex.getMessage(), ex);
        }
        return applicationConfiguration;
    }

    @Override
    public void saveConfigurationToFile(ApplicationConfiguration applicationConfiguration) {
        try {
            objectMapper.writeValue(new File(configFileName), applicationConfiguration);
        } catch (IOException ex) {
            LOGGER.error("Exception was occurred while saving configuration file: {}", ex.getMessage(), ex);
        }
    }
}
