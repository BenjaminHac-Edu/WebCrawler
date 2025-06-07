package com.webcrawler.crawler.core;

import com.webcrawler.crawler.checker.HttpStatusChecker;
import com.webcrawler.html.HtmlDocumentFetcher;

import java.util.Set;

public interface CrawlServices {
    HtmlDocumentFetcher getFetcher();
    HttpStatusChecker getChecker();
    CrawlResult getCrawlResult();
    Set<String> getVisitedUrls();
    TaskScheduler getScheduler();
    void countDown();
}
