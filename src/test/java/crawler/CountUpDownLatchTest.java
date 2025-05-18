package crawler;

import com.webcrawler.crawler.CountUpDownLatch;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.*;

public class CountUpDownLatchTest {

    private CountUpDownLatch latch;

    @BeforeEach
    void setUp() {
        latch = new CountUpDownLatch();
    }

    @Test
    void awaitReturnsImmediatelyWhenCountIsZero() throws InterruptedException {
        latch.await();
    }

    @Test
    void awaitBlocksUntilCountDown() throws InterruptedException, ExecutionException {
        latch.countUp();

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Boolean> future = executor.submit(() -> {
            Thread.sleep(200);
            latch.countDown();
            return true;
        });

        latch.await(); // Should block until countDown
        assertTrue(future.get());
        executor.shutdown();
    }

    @Test
    void countDownBelowZeroThrowsException() {
        CountUpDownLatch latch = new CountUpDownLatch();
        assertThrows(IllegalStateException.class, latch::countDown);
    }

    @Test
    void awaitUnblocksWhenMultipleCountDownsCalled() throws Exception {
        latch.countUp();
        latch.countUp();
        latch.countUp();

        ExecutorService executor = Executors.newCachedThreadPool();

        for (int i = 0; i < 3; i++) {
            executor.submit(() -> {
                try {
                    Thread.sleep(100);
                    latch.countDown();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }

        long start = System.currentTimeMillis();
        latch.await();
        long duration = System.currentTimeMillis() - start;

        assertTrue(duration >= 100, "Should wait for all countdowns");
        executor.shutdown();
    }

    @Test
    void countUpAndDownMaintainsCorrectCount() {
        for (int i = 0; i < 5; i++) {
            latch.countUp();
        }

        for (int i = 0; i < 5; i++) {
            latch.countDown();
        }

        assertDoesNotThrow(latch::await);
    }
}
