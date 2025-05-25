package crawler.model;

import com.webcrawler.crawler.model.BrokenLink;
import com.webcrawler.crawler.model.Heading;
import com.webcrawler.crawler.model.Link;
import com.webcrawler.crawler.core.PageElement;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PageElementTest {
    @Test
    void testLinkFieldsAndMarkdown() {
        Link link = new Link(2, 5, "http://parent.com", "http://target.com");

        assertEquals(2, link.getDepth());
        assertEquals(5, link.getOrder());
        assertEquals("http://parent.com", link.getParentUrl());
        assertEquals("http://target.com", link.getUrl());

        String markdown = link.toMarkdown("-->");
        assertEquals("<br>--> link to <a>http://target.com</a>", markdown);
    }

    @Test
    void testBrokenLinkFieldsAndMarkdown() {
        BrokenLink brokenLink = new BrokenLink(3, 10, "http://parent.com", "http://broken.com");

        assertEquals(3, brokenLink.getDepth());
        assertEquals(10, brokenLink.getOrder());
        assertEquals("http://parent.com", brokenLink.getParentUrl());
        assertEquals("http://broken.com", brokenLink.getUrl());

        String markdown = brokenLink.toMarkdown("--->");
        assertEquals("<br>---> broken link <a>http://broken.com</a>", markdown);
    }

    @Test
    void testHeadingFieldsAndMarkdown() {
        Heading heading = new Heading(1, 1, "http://parent.com", "h2", "Section Title");

        assertEquals(1, heading.getDepth());
        assertEquals(1, heading.getOrder());
        assertEquals("http://parent.com", heading.getParentUrl());

        String markdown = heading.toMarkdown("");
        assertEquals("##  Section Title", markdown);
    }

    @Test
    void testPageElementSortingByOrder() {
        PageElement el1 = new Link(1, 3, "http://x.com", "http://a.com");
        PageElement el2 = new Heading(1, 1, "http://x.com", "h1", "Title");
        PageElement el3 = new BrokenLink(1, 2, "http://x.com", "http://broken.com");

        List<PageElement> elements = Arrays.asList(el1, el2, el3);
        elements.sort(null); // uses compareTo()

        assertEquals(el2, elements.get(0)); // order = 1
        assertEquals(el3, elements.get(1)); // order = 2
        assertEquals(el1, elements.get(2)); // order = 3
    }

    @Test
    void testHeadingTagParsing() {
        Heading h3 = new Heading(1, 1, "http://x.com", "h3", "Subsection");
        String md = h3.toMarkdown(">");
        assertEquals("### > Subsection", md);
    }
}
