import com.webcrawler.crawler.*;
import com.webcrawler.html.HtmlDocument;
import com.webcrawler.html.HtmlDocumentFetcher;
import com.webcrawler.html.HtmlElement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CrawlerTest {

    private HtmlDocumentFetcher fetcher;
    private HtmlDocument mockDoc;
    private HttpStatusChecker statusChecker;
    private Crawler crawler;

    @BeforeEach
    void setup() {
        fetcher = mock(HtmlDocumentFetcher.class);
        mockDoc = mock(HtmlDocument.class);
        statusChecker = mock(HttpStatusChecker.class);
    }

    @Test
    void testCrawlAddsHeadingAndLink() throws IOException {
        HtmlElement heading = mock(HtmlElement.class);
        when(heading.getTagName()).thenReturn("h1");
        when(heading.getText()).thenReturn("Test Heading");

        HtmlElement link = mock(HtmlElement.class);
        when(link.getAbsoluteHref()).thenReturn("https://example.com/page");

        when(mockDoc.selectHeadings()).thenReturn(List.of(heading));
        when(mockDoc.selectLinks()).thenReturn(List.of(link));
        when(fetcher.fetch("https://example.com")).thenReturn(mockDoc);
        when(fetcher.fetch("https://example.com/page")).thenReturn(mockDoc);
        when(statusChecker.isBroken("https://example.com/page")).thenReturn(false);

        String[] domains = {"example.com"};
        CrawlerConfig config = new CrawlerConfig("https://example.com", 2, domains);
        crawler = new Crawler(config, fetcher, statusChecker);

        CrawlResult result = crawler.startCrawling();

        assertEquals(4, result.getElements().size());
        assertTrue(result.getElements().stream().anyMatch(e -> e instanceof Heading));
        assertTrue(result.getElements().stream().anyMatch(e -> e instanceof Link));
    }

    @Test
    void testBrokenLinkIsAddedOnIOException() throws IOException {
        when(fetcher.fetch("https://broken.com")).thenThrow(new IOException());

        CrawlerConfig config = new CrawlerConfig("https://broken.com", 1, new String[]{"broken.com"});
        crawler = new Crawler(config, fetcher, statusChecker);

        CrawlResult result = crawler.startCrawling();

        assertEquals(1, result.getElements().size());
        assertInstanceOf(BrokenLink.class, result.getElements().get(0));
    }
}
