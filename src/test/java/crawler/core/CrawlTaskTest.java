package crawler.core;

import com.webcrawler.crawler.checker.HttpStatusChecker;
import com.webcrawler.crawler.config.CrawlerConfig;
import com.webcrawler.crawler.core.*;
import com.webcrawler.crawler.model.BrokenLink;
import com.webcrawler.crawler.model.ErrorElement;
import com.webcrawler.html.HtmlDocument;
import com.webcrawler.html.HtmlDocumentFetcher;
import com.webcrawler.html.HtmlElement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class CrawlTaskTest {
    private HtmlDocumentFetcher fetcher;
    private HttpStatusChecker checker;
    private HtmlDocument document;
    private HtmlElement headingElement;
    private HtmlElement linkElement;
    private CrawlResult crawlResult;
    private Set<String> visitedUrls;
    private CountUpDownLatch latch;
    private ConcurrentCrawler scheduler;
    private CrawlerConfig config;

    @BeforeEach
    void setup() {
        fetcher = mock(HtmlDocumentFetcher.class);
        checker = mock(HttpStatusChecker.class);
        document = mock(HtmlDocument.class);
        headingElement = mock(HtmlElement.class);
        linkElement = mock(HtmlElement.class);
        crawlResult = new CrawlResult("https://test.com");
        visitedUrls = new CopyOnWriteArraySet<>();
        latch = mock(CountUpDownLatch.class);
        scheduler = mock(ConcurrentCrawler.class);
        config = mock(CrawlerConfig.class);

        when(config.getMaxDepth()).thenReturn(2);
        when(config.getAllowedDomains()).thenReturn(Set.of("test.com"));
    }

    @Test
    void testRun_addsHeadingsAndLinks() throws Exception {
        when(fetcher.fetch("https://test.com")).thenReturn(document);

        when(document.selectHeadings()).thenReturn(List.of(headingElement));
        when(headingElement.getTagName()).thenReturn("h1");
        when(headingElement.getText()).thenReturn("Title");

        when(document.selectLinks()).thenReturn(List.of(linkElement));
        when(linkElement.getAbsoluteHref()).thenReturn("https://test.com/page");
        when(checker.isBroken(anyString())).thenReturn(false);

        CrawlContext context = new CrawlContext(config, fetcher, checker, visitedUrls, crawlResult, scheduler, latch);
        new CrawlTask("https://test.com", 1, context).run();

        assertEquals(2, crawlResult.getSortedElements().size());
        verify(scheduler).submitChildTask(eq("https://test.com/page"), eq(2), any());
        verify(latch).countDown();
    }

    @Test
    void testRun_doesNotExceedMaxDepth() throws Exception {
        CrawlContext context = new CrawlContext(config, fetcher, checker, visitedUrls, crawlResult, scheduler, latch);
        new CrawlTask("https://test.com", 3, context).run();

        assertTrue(crawlResult.getSortedElements().isEmpty());
        verify(fetcher, never()).fetch(any());
        verify(latch).countDown();
    }

    @Test
    void testRun_doesNotRevisitUrls() throws Exception {
        visitedUrls.add("https://test.com");
        CrawlContext context = new CrawlContext(config, fetcher, checker, visitedUrls, crawlResult, scheduler, latch);

        new CrawlTask("https://test.com", 1, context).run();

        verify(fetcher, never()).fetch(any());
        verify(latch).countDown();
    }

    @Test
    void testRun_handlesBrokenLink() throws Exception {
        when(fetcher.fetch("https://test.com")).thenReturn(document);
        when(document.selectHeadings()).thenReturn(Collections.emptyList());
        when(document.selectLinks()).thenReturn(List.of(linkElement));
        when(linkElement.getAbsoluteHref()).thenReturn("https://test.com/broken");
        when(checker.isBroken("https://test.com/broken")).thenReturn(true);

        CrawlContext context = new CrawlContext(config, fetcher, checker, visitedUrls, crawlResult, scheduler, latch);
        new CrawlTask("https://test.com", 1, context).run();

        assertTrue(crawlResult.getSortedElements().stream().anyMatch(e -> e instanceof BrokenLink));
        verify(latch).countDown();
    }

    @Test
    void testRun_handlesFetcherExceptionGracefully() throws Exception {
        when(fetcher.fetch(anyString())).thenThrow(new IOException("Fetch failed"));

        CrawlContext context = new CrawlContext(config, fetcher, checker, visitedUrls, crawlResult, scheduler, latch);
        new CrawlTask("https://test.com", 1, context).run();

        assertTrue(crawlResult.getSortedElements().stream().anyMatch(e -> e instanceof ErrorElement));
        verify(latch).countDown();
    }

    @Test
    void testRun_skipsLinkOutsideAllowedDomain() throws Exception {
        when(fetcher.fetch("https://test.com")).thenReturn(document);
        when(document.selectLinks()).thenReturn(List.of(linkElement));
        when(linkElement.getAbsoluteHref()).thenReturn("https://otherdomain.com");
        when(document.selectHeadings()).thenReturn(Collections.emptyList());
        when(checker.isBroken(anyString())).thenReturn(false);

        CrawlContext context = new CrawlContext(config, fetcher, checker, visitedUrls, crawlResult, scheduler, latch);
        new CrawlTask("https://test.com", 1, context).run();

        assertTrue(crawlResult.getSortedElements().isEmpty());
        verify(scheduler, never()).submitChildTask(any(), anyInt(), any());
        verify(latch).countDown();
    }
}
