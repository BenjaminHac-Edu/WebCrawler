package crawler.model;

import com.webcrawler.crawler.model.BrokenLink;
import com.webcrawler.crawler.model.Heading;
import com.webcrawler.crawler.model.Link;
import com.webcrawler.crawler.model.PageElement;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PageElementTest {

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

}
