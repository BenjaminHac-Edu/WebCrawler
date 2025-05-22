package com.webcrawler.crawler;

import com.webcrawler.html.HtmlDocument;

import java.util.concurrent.atomic.AtomicInteger;

public class CrawlTask implements Runnable {
    private final String url;
    private final int depth;
    private final CrawlContext context;
    AtomicInteger orderNumber;

    public CrawlTask(String url, int depth, CrawlContext context) {
        this.url = url;
        this.depth = depth;
        this.context = context;
        orderNumber = new AtomicInteger(0);
    }

    @Override
    public void run() {
        try {
            if (depth > context.config().getMaxDepth() || context.visitedUrls().contains(url)) return;

            context.visitedUrls().add(url);

            HtmlDocument doc = context.fetcher().fetch(url);

            extractHeadings(doc, depth);
            extractLinks(doc, depth);

        } catch (Exception e) {
            context.crawlResult().addElement(new BrokenLink(depth, orderNumber.getAndIncrement(), url, url));
        } finally {
            context.latch().countDown();
        }
    }

    private void extractHeadings(HtmlDocument document, int depth) {

        document.selectHeadings().forEach(heading -> {
            context.crawlResult().addElement(new Heading(depth, orderNumber.getAndIncrement(), url, heading.getTagName(), heading.getText()));
        });
    }

    private void extractLinks(HtmlDocument document, int depth) {
        document.selectLinks().forEach(link -> {
            String href = link.getAbsoluteHref();
            if (href.isBlank()) return;

            if (context.checker().isBroken(href)) {
                context.crawlResult().addElement(new BrokenLink(depth, orderNumber.getAndIncrement(), url, href));
            } else if (isValidDomain(href)) {
                context.crawlResult().addElement(new Link(depth, orderNumber.getAndIncrement(), url, href));
                context.scheduler().submitChildTask(href, depth + 1, context.config(), context.latch());
            }
        });
    }

    private boolean isValidDomain(String url) {
        return context.config().getAllowedDomains().stream().anyMatch(url::contains);
    }
}
