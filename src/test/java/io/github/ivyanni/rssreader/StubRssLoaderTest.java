package io.github.ivyanni.rssreader;

import com.rometools.rome.feed.synd.SyndContent;
import com.rometools.rome.feed.synd.SyndContentImpl;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndEntryImpl;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.SyndFeedOutput;
import com.rometools.rome.io.XmlReader;
import io.github.ivyanni.rssreader.model.ApplicationConfiguration;
import io.github.ivyanni.rssreader.model.FeedConfiguration;
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
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
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
    public static void clean() throws Exception {
        new File(OUTPUT_FILENAME).delete();
        clearFeed(RSS_STUB_FILENAME);
        clearFeed(ATOM_STUB_FILENAME);
    }

    @Before
    public void deleteFile() {
        new File(OUTPUT_FILENAME).delete();
    }

    @Test
    public void testScheduleOneFeed() throws Exception {
        FeedConfiguration feedConfiguration = createStubFeed(RSS_STUB_FILENAME);
        feedUpdateSchedulerService.scheduleFeedUpdate(feedConfiguration);
        Thread.sleep(1000);
        addEntryToFeed(RSS_STUB_FILENAME, 2);
        Thread.sleep(3000);
        File file = new File(OUTPUT_FILENAME);
        assertTrue(file.exists());

        List<String> strings = FileUtils.readLines(file, StandardCharsets.UTF_8);
        assertEquals(2, strings.size());
        assertTrue(strings.stream().allMatch(str -> str.startsWith("title: ")));
        feedConfiguration.getSavedFuture().cancel(true);
    }

    @Test
    public void testScheduleMultipleFeeds() throws Exception {
        FeedConfiguration rssConfiguration = createStubFeed(RSS_STUB_FILENAME);
        FeedConfiguration atomConfiguration = createStubFeed(ATOM_STUB_FILENAME);
        feedUpdateSchedulerService.scheduleFeedUpdate(rssConfiguration);
        feedUpdateSchedulerService.scheduleFeedUpdate(atomConfiguration);
        Thread.sleep(1000);
        addEntryToFeed(RSS_STUB_FILENAME, 2);
        addEntryToFeed(ATOM_STUB_FILENAME, 3);
        Thread.sleep(3000);
        File file = new File(OUTPUT_FILENAME);
        assertTrue(file.exists());

        List<String> strings = FileUtils.readLines(file, StandardCharsets.UTF_8);
        assertEquals(5, strings.size());
        rssConfiguration.getSavedFuture().cancel(true);
        atomConfiguration.getSavedFuture().cancel(true);
    }

    @Test
    public void testScheduleIncorrectFeed() throws Exception {
        FeedConfiguration feedConfiguration =
                createStubFeed("http://incorrecturl-dgadfgadfg.ru/SDGSD.xml");
        feedUpdateSchedulerService.scheduleFeedUpdate(feedConfiguration);
        Thread.sleep(3000);
        File file = new File(OUTPUT_FILENAME);
        assertFalse(file.exists());
    }

    private static void clearFeed(String fileName) throws Exception {
        File file = new File(fileName);
        SyndFeed feed = new SyndFeedInput().build(new XmlReader(file));
        feed.getEntries().clear();
        new SyndFeedOutput().output(feed, file, true);
    }

    private void addEntryToFeed(String filePath, int amount) throws Exception {
        File file = new File(filePath);
        SyndFeed feed = new SyndFeedInput().build(new XmlReader(file));
        for (int i = 0; i < amount; i++) {
            feed.getEntries().add(createFeedEntry());
        }
        new SyndFeedOutput().output(feed, file, true);
    }

    private SyndEntry createFeedEntry() {
        SyndEntry entry = new SyndEntryImpl();
        entry.setTitle("TestTitle");
        SyndContent description = new SyndContentImpl();
        description.setValue("TestDescription");
        entry.setDescription(description);
        entry.setAuthor("TestAuthor");
        entry.setPublishedDate(Calendar.getInstance().getTime());
        return entry;
    }

    private FeedConfiguration createStubFeed(String feedUrlString) throws MalformedURLException {
        URL feedUrl = new File(feedUrlString).toURI().toURL();
        FeedConfiguration feedConfiguration = new FeedConfiguration(feedUrl, OUTPUT_FILENAME);
        feedConfiguration.setChunkSize(100L);
        feedConfiguration.setDelay(2L);
        feedConfiguration.setOutputParams(List.of("title"));
        applicationConfiguration.getFeedConfigurations().put(new Random().toString(), feedConfiguration);
        return feedConfiguration;
    }
}
