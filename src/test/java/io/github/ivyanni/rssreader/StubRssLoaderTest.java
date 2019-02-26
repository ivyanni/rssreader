package io.github.ivyanni.rssreader;

import io.github.ivyanni.rssreader.config.ApplicationConfiguration;
import io.github.ivyanni.rssreader.config.FeedConfiguration;
import io.github.ivyanni.rssreader.service.FeedUpdateSchedulerService;
import io.github.ivyanni.rssreader.service.impl.FeedUpdateSchedulerServiceImpl;
import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Ilia Vianni on 26.02.2019.
 */
public class StubRssLoaderTest {
    private static final String OUTPUT_FILENAME = "stub\\output.txt";
    private static final String RSS_STUB_FILENAME = "stub\\stub.rss";
    private static final String ATOM_STUB_FILENAME = "stub\\stub.atom";
    private static ApplicationConfiguration applicationConfiguration;
    private static FeedUpdateSchedulerService feedUpdateSchedulerService;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @BeforeClass
    public static void createStubAppConfiguration() {
        applicationConfiguration = new ApplicationConfiguration();
        applicationConfiguration.setCorePoolSize(2);
        feedUpdateSchedulerService = new FeedUpdateSchedulerServiceImpl(applicationConfiguration);
    }

    @AfterClass
    public static void deleteFile() {
        new File(OUTPUT_FILENAME).delete();
    }

    @Before
    public void removeAllFeeds() {
        applicationConfiguration.getFeedConfigurations().values().forEach(config -> {
            if (config.getSavedFuture() != null) {
                config.getSavedFuture().cancel(true);
            }
        });
        applicationConfiguration.getFeedConfigurations().clear();
        new File(OUTPUT_FILENAME).delete();
    }

    @Test
    public void testScheduleOneFeed() throws IOException, InterruptedException {
        feedUpdateSchedulerService.scheduleFeedUpdate(createStubFeed(RSS_STUB_FILENAME));
        Thread.sleep(2000);
        File file = new File(OUTPUT_FILENAME);
        assertTrue(file.exists());

        List<String> strings = FileUtils.readLines(file, StandardCharsets.UTF_8);
        assertEquals(2, strings.size());
        assertTrue(strings.stream().allMatch(str -> str.startsWith("title: ")));
    }

    @Test
    public void testScheduleMultipleFeeds() throws IOException, InterruptedException {
        feedUpdateSchedulerService.scheduleFeedUpdate(createStubFeed(RSS_STUB_FILENAME));
        feedUpdateSchedulerService.scheduleFeedUpdate(createStubFeed(ATOM_STUB_FILENAME));
        Thread.sleep(2000);
        File file = new File(OUTPUT_FILENAME);
        assertTrue(file.exists());

        List<String> strings = FileUtils.readLines(file, StandardCharsets.UTF_8);
        assertEquals(5, strings.size());
    }

    @Test
    public void testScheduleIncorrectFeed() throws MalformedURLException, InterruptedException {
        FeedConfiguration feedConfiguration =
                createStubFeed("http://incorrecturl-dgadfgadfg.ru/SDGSD.xml");
        feedUpdateSchedulerService.scheduleFeedUpdate(feedConfiguration);
        Thread.sleep(2000);
        File file = new File(OUTPUT_FILENAME);
        assertFalse(file.exists());
    }

    private FeedConfiguration createStubFeed(String feedUrlString) throws MalformedURLException {
        URL feedUrl = new File(feedUrlString).toURI().toURL();
        FeedConfiguration feedConfiguration = new FeedConfiguration(feedUrl, OUTPUT_FILENAME);
        feedConfiguration.setChunkSize(100L);
        feedConfiguration.setDelay(4L);
        feedConfiguration.setOutputParams(List.of("title"));
        applicationConfiguration.getFeedConfigurations().put(new Random().toString(), feedConfiguration);
        return feedConfiguration;
    }
}
