package com.webcrawler.crawler.core;

import com.webcrawler.crawler.config.CrawlerConfig;
import com.webcrawler.crawler.model.BrokenLink;
import com.webcrawler.crawler.model.ErrorElement;
import com.webcrawler.crawler.model.Heading;
import com.webcrawler.crawler.model.Link;
import com.webcrawler.html.HtmlDocument;

import java.util.concurrent.atomic.AtomicInteger;

public class CrawlTask implements Runnable {
    private final String url;
    private final int depth;
    private final CrawlerConfig config;
    private final CrawlServices services;
    private final AtomicInteger orderNumber;

    public CrawlTask(String url, int depth, CrawlerConfig config,
                     CrawlServices services) {
        this.url = url;
        this.depth = depth;
        this.config = config;
        this.services = services;
        this.orderNumber = new AtomicInteger(0);
    }

    @Override
    public void run() {
        try {
            if (depth > config.getMaxDepth()) return;
            if (!services.getVisitedUrls().add(url)) return; // atomic check-and-add

            HtmlDocument doc = services.getFetcher().fetch(url);

            extractHeadings(doc);
            extractLinks(doc);

        } catch (Exception e) {
            recordError("Unexpected error: " + e.getMessage());
        } finally {
            services.countDown();
        }
    }

    private void extractHeadings(HtmlDocument document) {
        try {
            document.selectHeadings().forEach(heading -> services.getCrawlResult().addElement(
                    new Heading(depth, orderNumber.getAndIncrement(), url, heading.getTagName(), heading.getText())
            ));
        } catch (Exception e) {
            recordError("Error extracting headings: " + e.getMessage());
        }
    }

    private void extractLinks(HtmlDocument document) {
        try {
            document.selectLinks().forEach(link -> {
                String href = link.getAbsoluteHref();
                if (href.isBlank()) return;

                checkLinks(href);
            });
        } catch (Exception e) {
            recordError("Error extracting links: " + e.getMessage());
        }
    }

    private void checkLinks(String href) {
        try {
            if (services.getChecker().isBroken(href)) {
                services.getCrawlResult().addElement(new BrokenLink(depth, orderNumber.getAndIncrement(), url, href));
                return;
            }
            if (!isValidDomain(href))
                return;

            services.getCrawlResult().addElement(new Link(depth, orderNumber.getAndIncrement(), url, href));
            services.getScheduler().submitTask(href, depth + 1, config);
        } catch (Exception e) {
            recordError("Error checking link: " + href + " - " + e.getMessage());
        }
    }

    private void recordError(String message) {
        services.getCrawlResult().addElement(new ErrorElement(
                depth, orderNumber.getAndIncrement(), url, "[ERROR] " + message
        ));
    }

    private boolean isValidDomain(String url) {
        return config.getAllowedDomains().stream().anyMatch(url::contains);
    }
}
