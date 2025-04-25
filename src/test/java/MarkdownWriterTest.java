import com.webcrawler.crawler.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MarkdownWriterTest {
    private CrawlResult result;

    @BeforeEach
    void setUp() {
        result = new CrawlResult("http://example.com");
    }

    @Test
    void testSingleHeadingAtDepth1() {
        result.addElement(new Heading(1, "Welcome"));

        String markdown = String.join("\n", MarkdownWriter.toMarkdownLines(result));

        assertTrue(markdown.contains("input: <a>http://example.com</a>"));
        assertTrue(markdown.contains("<br>depth: 1"));
        assertTrue(markdown.contains("#  Welcome"));
    }

    @Test
    void testMultipleElementsDifferentDepths() {
        result.addElement(new Heading(1, "Title"));
        result.addElement(new Link(1, "http://next.com"));
        result.addElement(new Heading(2, "Subsection"));
        result.addElement(new BrokenLink(2, "http://broken-link.com"));
        result.addElement(new Heading(3, "Sub-subsection"));

        String markdown = String.join("\n", MarkdownWriter.toMarkdownLines(result));

        assertTrue(markdown.contains("<br>depth: 1"));
        assertTrue(markdown.contains("#  Title"));
        assertTrue(markdown.contains("<br> link to <a>http://next.com</a>"));

        assertTrue(markdown.contains("<br>depth: 2"));
        assertTrue(markdown.contains("# --> Subsection"));
        assertTrue(markdown.contains("<br>--> broken link <a>http://broken-link.com</a>"));

        assertTrue(markdown.contains("<br>depth: 3"));
        assertTrue(markdown.endsWith("# ----> Sub-subsection"));
    }

    @Test
    void testIndentationFormatting() {
        result.addElement(new Heading(3, "Deep Header"));

        String markdown = String.join("\n", MarkdownWriter.toMarkdownLines(result));

        assertTrue(markdown.contains("<br>depth: 3"));
        assertTrue(markdown.contains("# ----> Deep Header"));
    }

    @Test
    void testEmptyCrawlResult() {
        String markdown = String.join("\n", MarkdownWriter.toMarkdownLines(result));

        assertEquals("input: <a>http://example.com</a>", markdown);
        assertEquals(1, markdown.lines().count());
    }
}
