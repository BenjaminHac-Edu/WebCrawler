package crawler.core;

import com.webcrawler.crawler.checker.HttpStatusChecker;
import com.webcrawler.crawler.config.CrawlerConfig;
import com.webcrawler.crawler.core.ConcurrentCrawler;
import com.webcrawler.crawler.core.CrawlResult;
import com.webcrawler.crawler.model.BrokenLink;
import com.webcrawler.crawler.model.Heading;
import com.webcrawler.crawler.model.Link;
import com.webcrawler.html.HtmlDocument;
import com.webcrawler.html.HtmlDocumentFetcher;
import com.webcrawler.html.HtmlElement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ConcurrentCrawlerTest {

    private HtmlDocumentFetcher fetcher;
    private HttpStatusChecker checker;

    @BeforeEach
    void setUp() {
        fetcher = mock(HtmlDocumentFetcher.class);
        checker = mock(HttpStatusChecker.class);
    }

    @Test
    void testConcurrentCrawling_multipleUrls_allProcessed() throws Exception {
        // Given
        CrawlerConfig config1 = new CrawlerConfig("http://site1.com", 2, Set.of("site1.com").toArray(new String[0]));
        CrawlerConfig config2 = new CrawlerConfig("http://site2.com", 2, Set.of("site2.com").toArray(new String[0]));

        HtmlDocument document1 = mock(HtmlDocument.class);
        HtmlDocument document2 = mock(HtmlDocument.class);

        HtmlElement heading = mock(HtmlElement.class);
        when(heading.getTagName()).thenReturn("h1");
        when(heading.getText()).thenReturn("Welcome");

        HtmlElement link1 = mock(HtmlElement.class);
        when(link1.getAbsoluteHref()).thenReturn("http://site.com/page1");
        when(link1.getTagName()).thenReturn("a");
        when(link1.getText()).thenReturn("Page 1");

        HtmlElement link2 = mock(HtmlElement.class);
        when(link2.getAbsoluteHref()).thenReturn("http://site.com/page2");
        when(link2.getTagName()).thenReturn("a");
        when(link2.getText()).thenReturn("Page 2");

        // Simulate different documents returned for different URLs
        when(fetcher.fetch("http://site1.com")).thenReturn(document1);
        when(fetcher.fetch("http://site1.com/page1")).thenReturn(document1);
        when(fetcher.fetch("http://site2.com")).thenReturn(document2);
        when(fetcher.fetch("http://site2.com/page2")).thenReturn(document2);

        when(document1.selectHeadings()).thenReturn(List.of(heading));
        when(document2.selectHeadings()).thenReturn(List.of(heading));
        when(document1.selectLinks()).thenReturn(List.of(link1));
        when(document2.selectLinks()).thenReturn(List.of(link2));

        when(link1.getAbsoluteHref()).thenReturn("http://site1.com/page1");
        when(link2.getAbsoluteHref()).thenReturn("http://site2.com/page2");

        when(checker.isBroken(anyString())).thenReturn(false);

        ConcurrentCrawler crawler = new ConcurrentCrawler(fetcher, checker, 4);
        CrawlResult result = crawler.start(List.of(config1, config2));

        assertNotNull(result);
        assertEquals("http://site1.com,http://site2.com", result.getStartUrl());

        long headingCount = result.getSortedElements().stream().filter(e -> e instanceof Heading).count();
        long linkCount = result.getSortedElements().stream().filter(e -> e instanceof Link).count();

        assertEquals(4, headingCount); // 1 heading * 4 fetches (2 sites + 2 links)
        assertEquals(4, linkCount);    // 1 link * 4 fetches
    }

    @Test
    void testBrokenLink_isRecordedAsBrokenLink() throws Exception {
        // Given
        CrawlerConfig config = new CrawlerConfig("http://site.com", 1, Set.of("site.com").toArray(new String[0]));
        HtmlDocument document = mock(HtmlDocument.class);
        HtmlElement link = mock(HtmlElement.class);
        when(link.getAbsoluteHref()).thenReturn("http://site.com/broken");
        when(link.getTagName()).thenReturn("a");
        when(link.getText()).thenReturn("Bad");

        when(fetcher.fetch("http://site.com")).thenReturn(document);
        when(document.selectHeadings()).thenReturn(List.of());
        when(document.selectLinks()).thenReturn(List.of(link));
        when(link.getAbsoluteHref()).thenReturn("http://site.com/broken");
        when(checker.isBroken("http://site.com/broken")).thenReturn(true);

        // When
        ConcurrentCrawler crawler = new ConcurrentCrawler(fetcher, checker, 2);
        CrawlResult result = crawler.start(List.of(config));

        // Then
        assertNotNull(result);
        long brokenCount = result.getSortedElements().stream().filter(e -> e instanceof BrokenLink).count();
        assertEquals(1, brokenCount);
    }

}
