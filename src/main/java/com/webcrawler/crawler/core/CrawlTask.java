package com.webcrawler.crawler.core;

import com.webcrawler.crawler.model.BrokenLink;
import com.webcrawler.crawler.model.ErrorElement;
import com.webcrawler.crawler.model.Heading;
import com.webcrawler.crawler.model.Link;
import com.webcrawler.html.HtmlDocument;

import java.util.concurrent.atomic.AtomicInteger;

public class CrawlTask implements Runnable {
    private final String url;
    private final int depth;
    private final CrawlContext context;
    private final AtomicInteger orderNumber;

    public CrawlTask(String url, int depth, CrawlContext context) {
        this.url = url;
        this.depth = depth;
        this.context = context;
        orderNumber = new AtomicInteger(0);
    }

    @Override
    public void run() {
        try {
            if (depth > context.config().getMaxDepth()) return;
            if (!context.visitedUrls().add(url)) return; // atomic check-and-add

            HtmlDocument doc = context.fetcher().fetch(url);

            extractHeadings(doc, depth);
            extractLinks(doc, depth);

        } catch (Exception e) {
            recordError("Unexpected error: " + e.getMessage());
        } finally {
            context.latch().countDown();
        }
    }

    private void extractHeadings(HtmlDocument document, int depth) {
        try {
            document.selectHeadings().forEach(heading -> {
                context.crawlResult().addElement(
                        new Heading(depth, orderNumber.getAndIncrement(), url, heading.getTagName(), heading.getText())
                );
            });
        } catch (Exception e) {
            recordError("Error extracting headings: " + e.getMessage());
        }
    }

    private void extractLinks(HtmlDocument document, int depth) {
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
            if (context.checker().isBroken(href)) {
                context.crawlResult().addElement(new BrokenLink(depth, orderNumber.getAndIncrement(), url, href));
                return;
            }
            if (!isValidDomain(href))
                return;

            context.crawlResult().addElement(new Link(depth, orderNumber.getAndIncrement(), url, href));
            context.scheduler().submitChildTask(href, depth + 1, context.config());
        } catch (Exception e) {
            recordError("Error checking link: " + href + " - " + e.getMessage());
        }
    }

    private void recordError(String message) {
        String errorMessage = "[ERROR] " + message;
        context.crawlResult().addElement(new ErrorElement(
                depth, orderNumber.getAndIncrement(), url, errorMessage
        ));
    }

    private boolean isValidDomain(String url) {
        return context.config().getAllowedDomains().stream().anyMatch(url::contains);
    }
}
