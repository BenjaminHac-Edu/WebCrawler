import com.webcrawler.crawler.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class CrawlerTest {
    private static class TestableCrawler extends Crawler {
        private final String htmlContent;

        public TestableCrawler(String url, int depth, String[] domains, String htmlContent) {
            super(url, depth, domains);
            this.htmlContent = htmlContent;
        }

        @Override
        protected Document fetchDocument(String url) throws IOException {
            return Jsoup.parse(htmlContent, url);
        }
    }

    private String html;
    private String[] domains;

    @BeforeEach
    void setUp() {
        html = """
            <html>
              <head><title>Test Page</title></head>
              <body>
                <h1>Main Heading</h1>
                <h2>Sub Heading</h2>
                <a href="http://allowed.com/page2.html">Page 2</a>
                <a href="http://blocked.com">Blocked Domain</a>
              </body>
            </html>
            """;
        domains = new String[] { "allowed.com" };
    }

    @Test
    void testCrawlAddsHeadings() {
        Crawler crawler = new TestableCrawler("http://allowed.com", 1, domains, html);

        CrawlResult result = crawler.startCrawling();

        assertEquals(2, result.getElements().stream().filter(e -> e instanceof Heading).count());
        assertTrue(result.getElements().stream().anyMatch(e -> e instanceof Heading && ((Heading) e).getContent().equals("Main Heading")));
    }

    @Test
    void testCrawlAddsValidLinksOnly() {
        Crawler crawler = new TestableCrawler("http://allowed.com", 1, domains, html);

        CrawlResult result = crawler.startCrawling();

        assertTrue(result.getElements().stream().anyMatch(e -> e instanceof Link && ((Link) e).getUrl().contains("page2")));
        assertFalse(result.getElements().stream().anyMatch(e -> e instanceof Link && ((Link) e).getUrl().contains("blocked.com")));
    }

    @Test
    void testRespectsMaxDepth() {
        String nestedHtml = """
            <html><body>
              <h1>Page 1</h1>
              <a href="http://allowed.com/page2.html">Next</a>
            </body></html>
            """;
        Crawler crawler = new TestableCrawler("http://allowed.com", 1, domains, nestedHtml);
        CrawlResult result = crawler.startCrawling();

        // Should not go beyond depth 1
        assertEquals(1, result.getElements().stream().filter(e -> e instanceof Link).count());
    }

    @Test
    void testBrokenLinkIsRecorded() {
        // Link that won't resolve or is known broken (simulate with override or force exception)
        Crawler crawler = new Crawler("http://nonexistent.com", 1, new String[]{"nonexistent.com"}) {
            @Override
            protected Document fetchDocument(String url) throws IOException {
                throw new IOException("Simulated failure");
            }
        };

        CrawlResult result = crawler.startCrawling();
        assertInstanceOf(BrokenLink.class, result.getElements().get(0));
        assertEquals("http://nonexistent.com", ((BrokenLink) result.getElements().get(0)).getUrl());
    }
}
