import com.webcrawler.crawler.UrlHelper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UrlHelperTest {
    @Test
    public void testWorkingLink() {
        // A working URL
        String url = "https://www.google.com";
        boolean result = UrlHelper.isBrokenLink(url);
        assertFalse(result, "Expected Google to be a working link");
    }

    @Test
    public void testBrokenLink() {
        // A fake URL to trigger a 404 or DNS error
        String url = "https://www.thispagedoesnotexist12345.com";
        boolean result = UrlHelper.isBrokenLink(url);
        assertTrue(result, "Expected non-existing URL to be broken");
    }

    @Test
    public void testMalformedUrl() {
        String url = "htp://bad-url";
        boolean result = UrlHelper.isBrokenLink(url);
        assertTrue(result, "Expected malformed URL to be treated as broken");
    }

    @Test
    public void testEmptyUrl() {
        String url = "";
        boolean result = UrlHelper.isBrokenLink(url);
        assertTrue(result, "Expected empty URL to be treated as broken");
    }

    @Test
    public void testNullUrl() {
        String url = null;
        boolean result = UrlHelper.isBrokenLink(url);
        assertTrue(result, "Expected null URL to be treated as broken");
    }
}
