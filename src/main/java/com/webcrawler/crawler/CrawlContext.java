package com.webcrawler.crawler;

import com.webcrawler.html.HtmlDocumentFetcher;

import java.util.Set;

public record CrawlContext(CrawlerConfig config, HtmlDocumentFetcher fetcher, HttpStatusChecker checker,
                           Set<String> visitedUrls, CrawlResult crawlResult, ConcurrentCrawler scheduler,
                           CountUpDownLatch latch) {
}
