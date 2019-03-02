package io.github.ivyanni.rssreader;

import io.github.ivyanni.rssreader.model.ApplicationConfiguration;
import io.github.ivyanni.rssreader.service.ConfigurationLoaderService;
import io.github.ivyanni.rssreader.service.impl.ConfigurationLoaderServiceImpl;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * @author Ilia Vianni on 26.02.2019.
 */
public class ConfigurationLoaderTest {
    private static final String STUB_CONFIG_PATH = "stub\\stubconfig.json";

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void testLoadConfig() {
        ConfigurationLoaderService configurationLoaderService = new ConfigurationLoaderServiceImpl(STUB_CONFIG_PATH);
        ApplicationConfiguration applicationConfiguration = configurationLoaderService.loadConfigurationFromFile();
        assertNotNull(applicationConfiguration);
        assertEquals(Integer.valueOf(2), applicationConfiguration.getCorePoolSize());
        assertEquals(2, applicationConfiguration.getFeedConfigurations().size());
    }

    @Test
    public void testSaveConfig() {
        String newConfigName = "stub\\stubconfig2.json";
        ApplicationConfiguration applicationConfiguration = new ApplicationConfiguration();
        applicationConfiguration.setCorePoolSize(2);
        ConfigurationLoaderService configurationLoaderService = new ConfigurationLoaderServiceImpl(newConfigName);
        configurationLoaderService.saveConfigurationToFile(applicationConfiguration);
        File file = new File(newConfigName);
        assertTrue(file.exists());
        file.delete();
    }
}
