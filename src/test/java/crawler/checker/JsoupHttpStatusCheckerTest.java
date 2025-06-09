package crawler.checker;

import com.webcrawler.crawler.checker.JsoupHttpStatusChecker;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class JsoupHttpStatusCheckerTest {
    private final JsoupHttpStatusChecker checker = new JsoupHttpStatusChecker();

    @Test
    void testNonBrokenUrl() throws IOException {
        String url = "https://www.example.com/";
        boolean isBroken = checker.isBroken(url);

        assertFalse(isBroken, "Expected example.com to not be broken");
    }

    @Test
    void testBrokenUrl() throws IOException {
        String url = "https://www.example.com/nonexistent-page";
        boolean isBroken = checker.isBroken(url);

        assertTrue(isBroken, "Expected nonexistent URL to be broken");
    }

    @Test
    void testMalformedUrlThrowsException() {
        String invalidUrl = "htp:// malformed_url";

        assertThrows(IllegalArgumentException.class, () -> checker.isBroken(invalidUrl));
    }

    @Test
    void testUnreachableUrlThrowsIOException() {
        String unreachableUrl = "http://nonexistent.openai.invalid";

        assertThrows(IOException.class, () -> checker.isBroken(unreachableUrl));
    }
}
