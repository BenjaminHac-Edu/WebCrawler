package crawler.model;

import com.webcrawler.crawler.model.BrokenLink;
import com.webcrawler.crawler.model.Link;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LinkTest {
    private Link link;

    @BeforeEach
    void setUp() {
        link = new Link(1, 1, "http://test.com", "http://x.com");
    }

    @Test
    void testGetters() {
        assertEquals("http://test.com", link.getParentUrl());
        assertEquals(1, link.getDepth());
        assertEquals("http://x.com", link.getUrl());
        assertEquals(1, link.getDepth());
    }

    @Test
    void testOrderingWithCompareTo() {
        Link link2 = new Link(2, 3, "http://test.com", "http://x.com");

        assertTrue(link.compareTo(link2) < 0);
        assertTrue(link2.compareTo(link) > 0);
        assertEquals(0, link.compareTo(new BrokenLink(1, 1, "http://test.com", "http://x.com")));
    }

    @Test
    void testToMarkdownOutput(){
        String markdown = link.toMarkdown("-->");
        assertEquals("<br>--> link to <a>http://x.com</a>", markdown);
    }

    @Test
    void TestToMarkdownHandlesEmptyMessage() {
        link = new Link(1, 1, "http://test.com", "");
        assertEquals("<br> link to <a></a>", link.toMarkdown(""));
    }
}
