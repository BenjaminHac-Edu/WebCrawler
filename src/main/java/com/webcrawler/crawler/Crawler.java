package com.webcrawler.crawler;

import com.webcrawler.html.HtmlDocument;
import com.webcrawler.html.HtmlDocumentFetcher;
import com.webcrawler.html.HtmlElement;

import java.io.IOException;
import java.util.*;

public class Crawler {
    private final CrawlerConfig config;
    private final HtmlDocumentFetcher fetcher;
    private final HttpStatusChecker statusChecker;

    private final Set<String> visitedUrls = new HashSet<>();
    private final CrawlResult crawlResult;


    public Crawler(CrawlerConfig config, HtmlDocumentFetcher fetcher, HttpStatusChecker statusChecker) {
        this.config = config;
        this.fetcher = fetcher;
        this.statusChecker = statusChecker;
        this.crawlResult = new CrawlResult(config.getStartUrl());
    }

    public CrawlResult startCrawling() {
        crawl(config.getStartUrl(), 1);
        return crawlResult;
    }

    private void crawl(String url, int depth) {
        if (depth > config.getMaxDepth() || visitedUrls.contains(url)) return;

        visitedUrls.add(url);

        try {
            HtmlDocument document = fetcher.fetch(url);
            extractHeadings(document, depth);
            extractLinks(document, depth);
        } catch (IOException e) {
            crawlResult.addElement(new BrokenLink(depth, url));
        }
    }

    private void extractHeadings(HtmlDocument document, int depth) {
        for (HtmlElement heading : document.selectHeadings()) {
            crawlResult.addElement(new Heading(depth, heading.getTagName(), heading.getText()));
        }
    }

    private void extractLinks(HtmlDocument document, int depth) {
        for (HtmlElement link : document.selectLinks()) {
            String linkHref = link.getAbsoluteHref();
            if (linkHref.isBlank()) continue;

            if (statusChecker.isBroken(linkHref)) {
                crawlResult.addElement(new BrokenLink(depth, linkHref));
            } else if (isValidDomain(linkHref)) {
                crawlResult.addElement(new Link(depth, linkHref));
                crawl(linkHref, depth + 1);
            }
        }
    }

    private boolean isValidDomain(String url) {
        return config.getAllowedDomains().stream().anyMatch(url::contains);
    }
}
