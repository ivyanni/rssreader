package io.github.ivyanni.rssreader.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.ivyanni.rssreader.config.ApplicationConfiguration;
import io.github.ivyanni.rssreader.service.ConfigurationLoaderService;

import java.io.File;
import java.io.IOException;

/**
 * @author Ilia Vianni on 23.02.2019.
 */
public class ConfigurationLoaderServiceImpl implements ConfigurationLoaderService {
    private static final String APP_CONFIG_PATH = "D:\\appconfig.json";
    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public ApplicationConfiguration loadConfigurationFromFile() {
        ApplicationConfiguration applicationConfiguration = new ApplicationConfiguration();
        File appConfigFile = new File(APP_CONFIG_PATH);
        try {
            if (appConfigFile.exists()) {
                applicationConfiguration = objectMapper.readValue(appConfigFile, ApplicationConfiguration.class);
            } else {
                appConfigFile.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return applicationConfiguration;
    }

    @Override
    public void saveConfigurationToFile(ApplicationConfiguration applicationConfiguration) {
        try {
            new ObjectMapper().writeValue(new File(APP_CONFIG_PATH), applicationConfiguration);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
