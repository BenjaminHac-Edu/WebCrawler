package com.webcrawler.crawler;

import com.webcrawler.html.HtmlDocumentFetcher;

import java.util.*;
import java.util.concurrent.*;

public class ConcurrentCrawler {

    private final HtmlDocumentFetcher fetcher;
    private final HttpStatusChecker checker;
    private final ExecutorService executor;

    private final Set<String> visitedUrls = Collections.synchronizedSet(new HashSet<>());
    private CrawlResult crawlResult = new CrawlResult("root");

    public ConcurrentCrawler(HtmlDocumentFetcher fetcher, HttpStatusChecker checker, int threadCount) {
        this.fetcher = fetcher;
        this.checker = checker;
        this.executor = Executors.newFixedThreadPool(threadCount);
    }

    public CrawlResult start(List<CrawlerConfig> configs) {
        CountUpDownLatch latch = new CountUpDownLatch();

        StringBuilder urlInput = new StringBuilder();
        for (CrawlerConfig config : configs){
            urlInput.append(config.getStartUrl()).append(",");
        }
        urlInput.deleteCharAt(urlInput.length() - 1);
        crawlResult = new CrawlResult(urlInput.toString());

        for (CrawlerConfig config : configs) {
            crawlResult.addRootUrl(config.getStartUrl());
            submitTask(config.getStartUrl(), 1, config, latch);
        }

        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            executor.shutdown();
        }

        return crawlResult;
    }

    private void submitTask(String url, int depth, CrawlerConfig config, CountUpDownLatch latch) {
        latch.countUp(); // Custom method to increment latch
        System.out.println("Submitting task " + url);
        CrawlContext context = new CrawlContext(config, fetcher, checker, visitedUrls, crawlResult, this, latch);
        executor.submit(new CrawlTask(url, depth, context));
    }

    public void submitChildTask(String url, int depth, CrawlerConfig config, CountUpDownLatch latch) {
        submitTask(url, depth, config, latch);
    }
}
