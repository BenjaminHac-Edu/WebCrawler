package com.webcrawler.crawler.core;

import com.webcrawler.crawler.checker.HttpStatusChecker;
import com.webcrawler.crawler.config.CrawlerConfig;
import com.webcrawler.html.HtmlDocumentFetcher;

import java.util.Set;

public record CrawlContext(CrawlerConfig config,
                           HtmlDocumentFetcher fetcher,
                           HttpStatusChecker checker,
                           Set<String> visitedUrls,
                           CrawlResult crawlResult,
                           ConcurrentCrawler scheduler,
                           CountUpDownLatch latch) {
}
