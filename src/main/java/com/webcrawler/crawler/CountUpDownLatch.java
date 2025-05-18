package com.webcrawler.crawler;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class CountUpDownLatch {
    private final AtomicInteger counter = new AtomicInteger(0);
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition zeroReached = lock.newCondition();

    public void countUp() {
        counter.incrementAndGet();
    }

    public void countDown() {
        int current = counter.decrementAndGet();
        if (current < 0) {
            throw new IllegalStateException("Count went below zero");
        }
        if (current == 0) {
            lock.lock();
            try {
                zeroReached.signalAll();
            } finally {
                lock.unlock();
            }
        }
    }

    public void await() throws InterruptedException {
        lock.lock();
        try {
            while (counter.get() > 0) {
                zeroReached.await();
            }
        } finally {
            lock.unlock();
        }
    }
}
