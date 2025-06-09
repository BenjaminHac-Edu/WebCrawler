package crawler.html;

import com.webcrawler.html.HtmlDocument;
import com.webcrawler.html.JsoupHtmlDocumentFetcher;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class JsoupHtmlDocumentFetcherTest {
    @Test
    void testFetchValidPage() throws Exception {
        JsoupHtmlDocumentFetcher fetcher = new JsoupHtmlDocumentFetcher();
        HtmlDocument doc = fetcher.fetch("https://example.com");

        assertNotNull(doc);
        assertFalse(doc.selectHeadings().isEmpty() || doc.selectLinks().isEmpty(), "Expected headings or links from example.com");
    }

    @Test
    void testFetchInvalidUrlThrowsException() {
        JsoupHtmlDocumentFetcher fetcher = new JsoupHtmlDocumentFetcher();
        assertThrows(Exception.class, () -> fetcher.fetch("http://invalid.invalid"), "Expected failure for unreachable URL");
    }
}
