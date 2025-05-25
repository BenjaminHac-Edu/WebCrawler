package com.webcrawler.crawler.core;

import com.webcrawler.crawler.checker.HttpStatusChecker;
import com.webcrawler.crawler.config.CrawlerConfig;
import com.webcrawler.html.HtmlDocumentFetcher;

import java.util.*;
import java.util.concurrent.*;

public class ConcurrentCrawler {

    private final HtmlDocumentFetcher fetcher;
    private final HttpStatusChecker checker;
    private final ExecutorService executor;
    private final CountUpDownLatch countLatch;

    private final Set<String> visitedUrls = Collections.synchronizedSet(new HashSet<>());
    private CrawlResult crawlResult;

    public ConcurrentCrawler(HtmlDocumentFetcher fetcher, HttpStatusChecker checker, int threadCount) {
        this.fetcher = fetcher;
        this.checker = checker;
        this.executor = Executors.newFixedThreadPool(threadCount);
        this.countLatch = new CountUpDownLatch();
    }

    public CrawlResult start(List<CrawlerConfig> configs) {
        createCrawlResult(configs);

        for (CrawlerConfig config : configs) {
            crawlResult.addRootUrl(config.getStartUrl());
            submitTask(config.getStartUrl(), 1, config);
        }

        try {
            countLatch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            executor.shutdown();
        }

        return crawlResult;
    }

    private void createCrawlResult(List<CrawlerConfig> configs) {
        StringBuilder urlInput = new StringBuilder();
        for (CrawlerConfig config : configs) {
            urlInput.append(config.getStartUrl()).append(",");
        }
        urlInput.deleteCharAt(urlInput.length() - 1);
        crawlResult = new CrawlResult(urlInput.toString());
    }

    private void submitTask(String url, int depth, CrawlerConfig config) {
        countLatch.countUp();

        System.out.println("Submitting task " + url);
        CrawlContext context = new CrawlContext(config, fetcher, checker, visitedUrls, crawlResult, this, countLatch);
        executor.submit(new CrawlTask(url, depth, context));
    }

    public void submitChildTask(String url, int depth, CrawlerConfig config) {
        submitTask(url, depth, config);
    }
}
