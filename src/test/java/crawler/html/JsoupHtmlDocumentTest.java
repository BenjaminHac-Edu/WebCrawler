package crawler.html;

import com.webcrawler.html.HtmlElement;
import com.webcrawler.html.JsoupHtmlDocument;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JsoupHtmlDocumentTest {
    private final String html = """
            <html>
              <head><title>Test Page</title></head>
              <body>
                <h1>Main Heading</h1>
                <h2>Subheading</h2>
                <a href="https://example.com">Example</a>
                <a href="/relative">Relative</a>
              </body>
            </html>
            """;

    @Test
    void testSelectHeadings() {
        Document doc = Jsoup.parse(html, "https://baseurl.com");
        JsoupHtmlDocument htmlDoc = new JsoupHtmlDocument(doc, "https://baseurl.com");

        List<HtmlElement> headings = htmlDoc.selectHeadings();
        assertEquals(2, headings.size());
        assertEquals("h1", headings.get(0).getTagName());
        assertEquals("Main Heading", headings.get(0).getText());
        assertEquals("h2", headings.get(1).getTagName());
        assertEquals("Subheading", headings.get(1).getText());
    }

    @Test
    void testSelectLinks() {
        Document doc = Jsoup.parse(html, "https://baseurl.com");
        JsoupHtmlDocument htmlDoc = new JsoupHtmlDocument(doc, "https://baseurl.com");

        List<HtmlElement> links = htmlDoc.selectLinks();
        assertEquals(2, links.size());
        assertEquals("a", links.get(0).getTagName());
        assertEquals("https://example.com", links.get(0).getAbsoluteHref());
        assertEquals("https://baseurl.com/relative", links.get(1).getAbsoluteHref());
    }
}
