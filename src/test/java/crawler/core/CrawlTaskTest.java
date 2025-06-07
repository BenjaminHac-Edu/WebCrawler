package crawler.core;

import com.webcrawler.crawler.checker.HttpStatusChecker;
import com.webcrawler.crawler.config.CrawlerConfig;
import com.webcrawler.crawler.core.*;
import com.webcrawler.crawler.model.*;
import com.webcrawler.html.HtmlDocument;
import com.webcrawler.html.HtmlDocumentFetcher;
import com.webcrawler.html.HtmlElement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class CrawlTaskTest {
    private CrawlServices services;
    private HtmlDocumentFetcher fetcher;
    private HttpStatusChecker checker;
    private HtmlDocument document;
    private CrawlResult result;
    private TaskScheduler scheduler;
    private Set<String> visitedUrls;
    private CrawlerConfig config;

    private final String url = "http://example.com";

    @BeforeEach
    void setup() {
        services = mock(CrawlServices.class);
        fetcher = mock(HtmlDocumentFetcher.class);
        checker = mock(HttpStatusChecker.class);
        document = mock(HtmlDocument.class);
        result = new CrawlResult(url);
        scheduler = mock(TaskScheduler.class);
        visitedUrls = Collections.synchronizedSet(new HashSet<>());

        config = new CrawlerConfig(url, 3, new String[]{"example.com"});

        when(services.getFetcher()).thenReturn(fetcher);
        when(services.getChecker()).thenReturn(checker);
        when(services.getVisitedUrls()).thenReturn(visitedUrls);
        when(services.getCrawlResult()).thenReturn(result);
        when(services.getScheduler()).thenReturn(scheduler);
    }

    @Test
    void testDepthExceeded() {
        CrawlTask task = new CrawlTask(url, 4, config, services);
        task.run();
        verify(services).countDown();
        assertTrue(result.getSortedElements().isEmpty());
    }

    @Test
    void testAlreadyVisitedUrl() throws IOException {
        visitedUrls.add(url);
        CrawlTask task = new CrawlTask(url, 1, config, services);
        task.run();
        verify(services).countDown();
        verify(fetcher, never()).fetch(anyString());
    }

    @Test
    void testSuccessfulCrawlWithHeadingsAndLinks() throws Exception {
        HtmlElement heading = mock(HtmlElement.class);
        when(heading.getTagName()).thenReturn("h1");
        when(heading.getText()).thenReturn("Title");

        HtmlElement link = mock(HtmlElement.class);
        when(link.getAbsoluteHref()).thenReturn("http://example.com/page");

        when(fetcher.fetch(url)).thenReturn(document);
        when(document.selectHeadings()).thenReturn(List.of(heading));
        when(document.selectLinks()).thenReturn(List.of(link));
        when(checker.isBroken(anyString())).thenReturn(false);

        CrawlTask task = new CrawlTask(url, 1, config, services);
        task.run();

        List<PageElement> elements = result.getSortedElements();
        assertEquals(2, elements.size()); // 1 heading + 1 link

        assertTrue(elements.stream().anyMatch(e -> e instanceof Heading));
        assertTrue(elements.stream().anyMatch(e -> e instanceof Link));

        verify(scheduler).submitTask("http://example.com/page", 2, config);
        verify(services).countDown();
    }

    @Test
    void testBrokenLinkDetected() throws Exception {
        HtmlElement link = mock(HtmlElement.class);
        when(link.getAbsoluteHref()).thenReturn("http://example.com/broken");

        when(fetcher.fetch(url)).thenReturn(document);
        when(document.selectHeadings()).thenReturn(List.of());
        when(document.selectLinks()).thenReturn(List.of(link));
        when(checker.isBroken("http://example.com/broken")).thenReturn(true);

        CrawlTask task = new CrawlTask(url, 1, config, services);
        task.run();

        List<PageElement> elements = result.getSortedElements();
        assertEquals(1, elements.size());
        assertTrue(elements.get(0) instanceof BrokenLink);
        verify(scheduler, never()).submitTask(any(), anyInt(), any());
    }

    @Test
    void testInvalidDomainLinkIgnored() throws Exception {
        HtmlElement link = mock(HtmlElement.class);
        when(link.getAbsoluteHref()).thenReturn("http://otherdomain.com/page");

        when(fetcher.fetch(url)).thenReturn(document);
        when(document.selectHeadings()).thenReturn(List.of());
        when(document.selectLinks()).thenReturn(List.of(link));
        when(checker.isBroken(any())).thenReturn(false);

        CrawlTask task = new CrawlTask(url, 1, config, services);
        task.run();

        assertTrue(result.getSortedElements().isEmpty());
        verify(scheduler, never()).submitTask(any(), anyInt(), any());
    }

    @Test
    void testFetcherThrowsException() throws Exception {
        when(fetcher.fetch(url)).thenThrow(new RuntimeException("Fetch error"));

        CrawlTask task = new CrawlTask(url, 1, config, services);
        task.run();

        List<PageElement> elements = result.getSortedElements();
        assertEquals(1, elements.size());
        assertTrue(elements.get(0) instanceof ErrorElement);
        assertTrue(((ErrorElement) elements.get(0)).getMessage().contains("Fetch error"));
    }

}
