import com.webcrawler.crawler.*;
import com.webcrawler.output.MarkdownWriter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MarkdownWriterTest {

    @Test
    void testToMarkdownLines_singleRootUrlWithHeadingsAndLinks() {
        CrawlResult result = new CrawlResult("http://example.com");
        result.addRootUrl("http://example.com");

        result.addElement(new Heading(1, 1, "http://example.com", "h1", "Welcome"));
        result.addElement(new Link(2, 2, "http://example.com", "http://example.com/about"));
        result.addElement(new BrokenLink(2, 3, "http://example.com", "http://example.com/missing"));

        List<String> markdown = MarkdownWriter.toMarkdownLines(result);

        assertEquals("input: <a>http://example.com</a>", markdown.get(0));
        assertTrue(markdown.contains("### Results for: <a>http://example.com</a>"));
        assertTrue(markdown.contains("<br>depth: 1"));
        assertTrue(markdown.stream().anyMatch(l -> l.contains("Welcome")));
        assertTrue(markdown.stream().anyMatch(l -> l.contains("link to <a>http://example.com/about</a>")));
        assertTrue(markdown.stream().anyMatch(l -> l.contains("broken link <a>http://example.com/missing</a>")));
    }

    @Test
    void testToMarkdownLines_multipleRootUrlsSeparatesResults() {
        CrawlResult result = new CrawlResult("http://root1.com,http://root2.com");
        result.addRootUrl("http://root1.com");
        result.addRootUrl("http://root2.com");

        result.addElement(new Heading(1, 1, "http://root1.com", "h1", "Root 1 Heading"));
        result.addElement(new Heading(1, 2, "http://root2.com", "h1", "Root 2 Heading"));

        List<String> markdown = MarkdownWriter.toMarkdownLines(result);

        assertTrue(markdown.contains("### Results for: <a>http://root1.com</a>"));
        assertTrue(markdown.contains("### Results for: <a>http://root2.com</a>"));
        assertTrue(markdown.stream().anyMatch(l -> l.contains("Root 1 Heading")));
        assertTrue(markdown.stream().anyMatch(l -> l.contains("Root 2 Heading")));

        int root1Index = markdown.indexOf("### Results for: <a>http://root1.com</a>");
        int root2Index = markdown.indexOf("### Results for: <a>http://root2.com</a>");
        assertTrue(root1Index < root2Index); // ensure ordering
    }

    @Test
    void testToMarkdownLines_skipsElementsWithoutMatchingRoot() {
        CrawlResult result = new CrawlResult("http://example.com");
        result.addRootUrl("http://example.com");

        // This parentUrl does NOT match root
        result.addElement(new Heading(1, 1, "http://other.com", "h1", "Should not appear"));

        List<String> markdown = MarkdownWriter.toMarkdownLines(result);

        assertFalse(markdown.stream().anyMatch(l -> l.contains("Should not appear")));
    }

    @Test
    void testToMarkdownLines_respectsDepthChanges() {
        CrawlResult result = new CrawlResult("http://example.com");
        result.addRootUrl("http://example.com");

        result.addElement(new Heading(1, 1, "http://example.com", "h1", "Depth 1 Heading"));
        result.addElement(new Heading(2, 2, "http://example.com", "h2", "Depth 2 Heading"));

        List<String> markdown = MarkdownWriter.toMarkdownLines(result);

        assertTrue(markdown.contains("<br>depth: 1"));
        assertTrue(markdown.contains("<br>depth: 2"));
    }

    @Test
    void testToMarkdownLines_indentsAccordingToDepth() {
        CrawlResult result = new CrawlResult("http://example.com");
        result.addRootUrl("http://example.com");

        result.addElement(new Heading(3, 1, "http://example.com", "h3", "Indented Heading"));

        List<String> markdown = MarkdownWriter.toMarkdownLines(result);
        assertTrue(markdown.stream().anyMatch(l -> l.contains("-->Indented Heading") || l.contains("--> Indented Heading")));
    }

    @Test
    public void testWriteToFile() throws IOException {
        CrawlResult result = new CrawlResult("http://example.com");
        String filename = "testfile.md";
        List<String> content = MarkdownWriter.toMarkdownLines(result);
        String expected = String.join("", content);

        MarkdownWriter.saveToMarkdown(filename, content);

        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line = reader.readLine();
            assertEquals(expected, line);
        }finally{
            File file = new File(filename);
            file.deleteOnExit();
        }
    }
}
