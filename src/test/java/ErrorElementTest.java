import com.webcrawler.crawler.ErrorElement;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ErrorElementTest {

    @Test
    void testToMarkdownOutput() {
        ErrorElement error = new ErrorElement(2, 1, "http://example.com", "Failed to parse");
        String markdown = error.toMarkdown("-->");
        assertEquals("<br>-->  Error: Failed to parse", markdown);
    }

    @Test
    void testGetters() {
        ErrorElement error = new ErrorElement(3, 5, "http://page.com", "Something broke");
        assertEquals(3, error.getDepth());
        assertEquals(5, error.getOrder());
        assertEquals("http://page.com", error.getParentUrl());
        assertEquals("Something broke", error.getMessage());
    }

    @Test
    void testOrderingWithCompareTo() {
        ErrorElement e1 = new ErrorElement(1, 2, "url", "Error A");
        ErrorElement e2 = new ErrorElement(1, 5, "url", "Error B");

        assertTrue(e1.compareTo(e2) < 0);
        assertTrue(e2.compareTo(e1) > 0);
        assertEquals(0, e1.compareTo(new ErrorElement(1, 2, "url", "Error A")));
    }

    @Test
    void testMarkdownIndentationVariation() {
        ErrorElement error = new ErrorElement(4, 0, "http://x.com", "Nested error");
        String indent = "--->>";
        assertEquals("<br>--->>  Error: Nested error", error.toMarkdown(indent));
    }

    @Test
    void testToMarkdownHandlesEmptyMessage() {
        ErrorElement error = new ErrorElement(1, 0, "http://x.com", "");
        assertEquals("<br>  Error: ", error.toMarkdown(""));
    }
}
