package crawler.model;

import com.webcrawler.crawler.model.BrokenLink;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BrokenLinkTest {

    private BrokenLink brokenLink;

    @BeforeEach
    void setUp() {
        brokenLink = new BrokenLink(1, 1, "http://test.com", "http://x.com");
    }

    @Test
    void testGetters() {
        assertEquals("http://test.com", brokenLink.getParentUrl());
        assertEquals(1, brokenLink.getDepth());
        assertEquals("http://x.com", brokenLink.getUrl());
        assertEquals(1, brokenLink.getDepth());
    }

    @Test
    void testOrderingWithCompareTo() {
        BrokenLink brokenLink2 = new BrokenLink(2, 3, "http://test.com", "http://x.com");

        assertTrue(brokenLink.compareTo(brokenLink2) < 0);
        assertTrue(brokenLink2.compareTo(brokenLink) > 0);
        assertEquals(0, brokenLink.compareTo(new BrokenLink(1, 1, "http://test.com", "http://x.com")));
    }

    @Test
    void testToMarkdownOutput() {
        String markdown = brokenLink.toMarkdown("-->");
        assertEquals("<br>--> broken link <a>http://x.com</a>", markdown);
    }

    @Test
    void TestToMarkdownHandlesEmptyMessage() {
        brokenLink = new BrokenLink(1, 1, "http://test.com", "");
        assertEquals("<br> broken link <a></a>", brokenLink.toMarkdown(""));
    }

}
