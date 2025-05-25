package crawler.model;

import com.webcrawler.crawler.model.ErrorElement;
import com.webcrawler.crawler.model.Link;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ErrorElementTest {

    private ErrorElement errorElement;

    @BeforeEach
    void setUp() {
        errorElement = new ErrorElement(1, 2, "http://test.com", "Error A");
    }

    @Test
    void testGetters() {
        assertEquals(1, errorElement.getDepth());
        assertEquals(2, errorElement.getOrder());
        assertEquals("http://test.com", errorElement.getParentUrl());
        assertEquals("Error A", errorElement.getMessage());
    }

    @Test
    void testOrderingWithCompareTo() {
        ErrorElement errorElement2 = new ErrorElement(1, 5, "url", "Error B");

        assertTrue(errorElement.compareTo(errorElement2) < 0);
        assertTrue(errorElement2.compareTo(errorElement) > 0);
        assertEquals(0, errorElement.compareTo(new ErrorElement(1, 2, "http://test.com", "Error A")));
    }

    @Test
    void testToMarkdownOutput() {
        String markdown = errorElement.toMarkdown("-->");
        assertEquals("<br>-->  Error: Error A", markdown);
    }

    @Test
    void testToMarkdownHandlesEmptyMessage() {
        errorElement = new ErrorElement(1, 0, "http://x.com", "");
        assertEquals("<br>  Error: ", errorElement.toMarkdown(""));
    }
}
