package output;

import com.webcrawler.crawler.core.CrawlResult;
import com.webcrawler.crawler.model.BrokenLink;
import com.webcrawler.crawler.model.Heading;
import com.webcrawler.crawler.model.Link;
import com.webcrawler.output.MarkdownWriter;
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

        assertLineEquals(markdown, 0, "input: <a>http://example.com</a>");
        assertContains(markdown, "### Results for: <a>http://example.com</a>");
        assertContains(markdown, "<br>depth: 1");
        assertContainsText(markdown, "Welcome");
        assertContains(markdown, "link to <a>http://example.com/about</a>");
        assertContains(markdown, "broken link <a>http://example.com/missing</a>");
    }

    @Test
    void testToMarkdownLines_multipleRootUrlsSeparatesResults() {
        CrawlResult result = new CrawlResult("http://root1.com,http://root2.com");
        result.addRootUrl("http://root1.com");
        result.addRootUrl("http://root2.com");

        result.addElement(new Heading(1, 1, "http://root1.com", "h1", "Root 1 Heading"));
        result.addElement(new Heading(1, 2, "http://root2.com", "h1", "Root 2 Heading"));

        List<String> markdown = MarkdownWriter.toMarkdownLines(result);

        assertContains(markdown, "### Results for: <a>http://root1.com</a>");
        assertContains(markdown, "### Results for: <a>http://root2.com</a>");
        assertContainsText(markdown, "Root 1 Heading");
        assertContainsText(markdown, "Root 2 Heading");

        assertAppearsInOrder(markdown,
                "### Results for: <a>http://root1.com</a>",
                "### Results for: <a>http://root2.com</a>"
        );
    }

    @Test
    void testToMarkdownLines_respectsDepthChanges() {
        CrawlResult result = new CrawlResult("http://example.com");
        result.addRootUrl("http://example.com");

        result.addElement(new Heading(1, 1, "http://example.com", "h1", "Depth 1 Heading"));
        result.addElement(new Heading(2, 2, "http://example.com", "h2", "Depth 2 Heading"));

        List<String> markdown = MarkdownWriter.toMarkdownLines(result);

        assertContains(markdown, "<br>depth: 1");
        assertContains(markdown, "<br>depth: 2");
    }

    @Test
    void testToMarkdownLines_indentsAccordingToDepth() {
        CrawlResult result = new CrawlResult("http://example.com");
        result.addRootUrl("http://example.com");

        result.addElement(new Heading(3, 1, "http://example.com", "h3", "Indented Heading"));

        List<String> markdown = MarkdownWriter.toMarkdownLines(result);

        assertAnyLineContains(markdown, "-->Indented Heading", "--> Indented Heading");
    }

    @Test
    void testDeepElementsAreIncludedInMarkdownOutput() {
        CrawlResult result = new CrawlResult("http://site.com");
        result.addRootUrl("http://site.com");

        result.addElement(new Heading(1, 0, "http://site.com", "h1", "Top Heading"));
        result.addElement(new Link(2, 1, "http://site.com/about", "http://site.com/about/team"));
        result.addElement(new BrokenLink(3, 2, "http://site.com/about/team", "http://site.com/broken"));

        List<String> lines = MarkdownWriter.toMarkdownLines(result);

        assertContains(lines, "depth: 3");
        assertContainsText(lines, "broken link");
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
        } finally {
            new File(filename).deleteOnExit();
        }
    }

    private void assertLineEquals(List<String> lines, int index, String expected) {
        assertTrue(index < lines.size(), "Line index out of range");
        assertEquals(expected, lines.get(index));
    }

    private void assertContains(List<String> lines, String expectedSubstring) {
        assertTrue(lines.stream().anyMatch(l -> l.contains(expectedSubstring)),
                "Expected to find: " + expectedSubstring);
    }

    private void assertContainsText(List<String> lines, String text) {
        assertTrue(lines.stream().anyMatch(line -> line.contains(text)),
                "Expected some line to contain text: " + text);
    }

    private void assertAnyLineContains(List<String> lines, String... substrings) {
        for (String s : substrings) {
            if (lines.stream().anyMatch(line -> line.contains(s))) return;
        }
        fail("Expected at least one line to contain one of: " + String.join(", ", substrings));
    }

    private void assertAppearsInOrder(List<String> lines, String first, String second) {
        int idx1 = lines.indexOf(first);
        int idx2 = lines.indexOf(second);
        assertTrue(idx1 != -1 && idx2 != -1 && idx1 < idx2,
                "Expected to find first string before second: " + first + " before " + second);
    }
}
