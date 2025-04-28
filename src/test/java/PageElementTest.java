import com.webcrawler.crawler.BrokenLink;
import com.webcrawler.crawler.Heading;
import com.webcrawler.crawler.Link;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PageElementTest {
    @Test
    void testHeadingDepth() {
        Heading heading = new Heading(2, "h1","Section Title");
        assertEquals(2, heading.getDepth());
    }

    @Test
    void testHeadingToMarkdown() {
        Heading heading = new Heading(2, "h1", "Section Title");
        String result = heading.toMarkdown("-->");
        assertEquals("# --> Section Title", result);
    }

    @Test
    void testLinkDepth() {
        Link link = new Link(1, "https://example.com");
        assertEquals(1, link.getDepth());
    }

    @Test
    void testLinkToMarkdown() {
        Link link = new Link(1, "https://example.com");
        String result = link.toMarkdown("");
        assertEquals("<br> link to <a>https://example.com</a>", result);
    }

    @Test
    void testBrokenLinkDepth() {
        BrokenLink broken = new BrokenLink(3, "https://404.com");
        assertEquals(3, broken.getDepth());
    }

    @Test
    void testBrokenLinkToMarkdown() {
        BrokenLink broken = new BrokenLink(3, "https://404.com");
        String result = broken.toMarkdown("---->");
        assertEquals("<br>----> broken link <a>https://404.com</a>", result);
    }
}
