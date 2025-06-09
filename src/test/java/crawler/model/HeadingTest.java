package crawler.model;

import com.webcrawler.crawler.model.Heading;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HeadingTest {

    private Heading heading;

    @BeforeEach
    void setUp() {
        heading = new Heading(1, 1, "http://test.com", "h1", "Section Title");
    }

    @Test
    void testGetters() {
        assertEquals("http://test.com", heading.getParentUrl());
        assertEquals(1, heading.getDepth());
        assertEquals(1, heading.getDepth());
    }

    @Test
    void testOrderingWithCompareTo() {
        Heading heading2 = new Heading(2, 3, "http://test.com", "h2", "Section Title");

        assertTrue(heading.compareTo(heading2) < 0);
        assertTrue(heading2.compareTo(heading) > 0);
        assertEquals(0, heading.compareTo(new Heading(1, 1, "http://test.com", "h2", "Section Title")));
    }

    @Test
    void testToMarkdownOutput() {
        String markdown = heading.toMarkdown("-->");
        assertEquals("# --> Section Title", markdown);
    }

    @Test
    void TestToMarkdownHandlesEmptyMessage() {
        heading = new Heading(1, 1, "http://test.com", "h2", "");
        assertEquals("##  ", heading.toMarkdown(""));
    }
}
