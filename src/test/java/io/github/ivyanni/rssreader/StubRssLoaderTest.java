package io.github.ivyanni.rssreader;

import io.github.ivyanni.rssreader.config.ApplicationConfiguration;
import io.github.ivyanni.rssreader.config.FeedConfiguration;
import io.github.ivyanni.rssreader.service.FeedUpdateService;
import io.github.ivyanni.rssreader.service.impl.FeedUpdateServiceImpl;
import org.apache.commons.io.FileUtils;
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
    private static ApplicationConfiguration applicationConfiguration;
    private static FeedUpdateService feedUpdateService;
    private String fileName = "D:\\file.txt";
    private String stubRssName = "file:\\\\\\D:\\rss.xml";
    private String stubSecondRssName = "file:\\\\\\D:\\rss2.xml";

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @BeforeClass
    public static void createStubAppConfiguration() {
        applicationConfiguration = new ApplicationConfiguration();
        applicationConfiguration.setCorePoolSize(2);
        feedUpdateService = new FeedUpdateServiceImpl(applicationConfiguration);
    }

    @Before
    public void removeAllFeeds() {
        applicationConfiguration.getFeedConfigurations().values().forEach(config -> {
            if(config.getScheduledFuture() != null) {
                config.getScheduledFuture().cancel(true);
            }
        });
        applicationConfiguration.getFeedConfigurations().clear();
        new File(fileName).delete();
    }

    @Test
    public void testScheduleOneFeed() throws IOException, InterruptedException {
        feedUpdateService.scheduleFeedUpdate(createStubFeed(new URL(stubRssName), 2L));
        Thread.sleep(2000);
        File file = new File(fileName);
        assertTrue(file.exists());

        List<String> strings = FileUtils.readLines(file, StandardCharsets.UTF_8);
        assertEquals(2, strings.size());
        assertTrue(strings.stream().allMatch(str -> str.startsWith("title: ")));
    }

    @Test
    public void testScheduleMultipleFeeds() throws IOException, InterruptedException {
        feedUpdateService.scheduleFeedUpdate(createStubFeed(new URL(stubRssName), 2L));
        feedUpdateService.scheduleFeedUpdate(createStubFeed(new URL(stubSecondRssName), 3L));
        Thread.sleep(2000);
        File file = new File(fileName);
        assertTrue(file.exists());

        List<String> strings = FileUtils.readLines(file, StandardCharsets.UTF_8);
        assertEquals(5, strings.size());
    }

    @Test
    public void testScheduleIncorrectFeed() throws MalformedURLException, InterruptedException {
        FeedConfiguration feedConfiguration = createStubFeed(new URL("http://dgadfgadfg.ru/SDGSD.xml"), 2L);
        feedUpdateService.scheduleFeedUpdate(feedConfiguration);
        Thread.sleep(2000);
        File file = new File(fileName);
        assertFalse(file.exists());
    }

    private FeedConfiguration createStubFeed(URL feedUrl, Long amount) {
        FeedConfiguration feedConfiguration = new FeedConfiguration();
        feedConfiguration.setItemsAmount(amount);
        feedConfiguration.setTimeout(4L);
        feedConfiguration.setParams(List.of("title"));
        feedConfiguration.setFeedUrl(feedUrl);
        feedConfiguration.setFilename(fileName);
        applicationConfiguration.getFeedConfigurations().put(new Random().toString(), feedConfiguration);
        return feedConfiguration;
    }
}
