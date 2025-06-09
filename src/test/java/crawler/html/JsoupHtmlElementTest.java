package crawler.html;

import com.webcrawler.html.JsoupHtmlElement;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class JsoupHtmlElementTest {
    @Test
    void testGetTagNameTextAndHref() {
        String html = "<a href=\"https://example.com\">Click me</a>";
        Element element = Jsoup.parse(html, "https://baseurl.com").selectFirst("a");

        assertNotNull(element);
        JsoupHtmlElement htmlElement = new JsoupHtmlElement(element);

        assertEquals("a", htmlElement.getTagName());
        assertEquals("Click me", htmlElement.getText());
        assertEquals("https://example.com", htmlElement.getAbsoluteHref());
    }

    @Test
    void testRelativeHrefResolution() {
        String html = "<a href=\"/path\">Link</a>";
        Element element = Jsoup.parse(html, "https://site.org").selectFirst("a");

        JsoupHtmlElement htmlElement = new JsoupHtmlElement(element);
        assertEquals("https://site.org/path", htmlElement.getAbsoluteHref());
    }
}
