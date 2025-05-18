package com.webcrawler.crawler;

import com.webcrawler.html.HtmlDocument;

public class CrawlTask implements Runnable {
    private final String url;
    private final int depth;
    private final CrawlContext context;

    public CrawlTask(String url, int depth, CrawlContext context) {
        this.url = url;
        this.depth = depth;
        this.context = context;
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
            context.crawlResult().addElement(new BrokenLink(depth, url));
        } finally {
            context.latch().countDown();
        }
    }

    private void extractHeadings(HtmlDocument document, int depth) {
        document.selectHeadings().forEach(heading -> {
            context.crawlResult().addElement(new Heading(depth, heading.getTagName(), heading.getText()));
        });
    }

    private void extractLinks(HtmlDocument document, int depth) {
        document.selectLinks().forEach(link -> {
            String href = link.getAbsoluteHref();
            if (href.isBlank()) return;

            if (context.checker().isBroken(href)) {
                context.crawlResult().addElement(new BrokenLink(depth, href));
            } else if (isValidDomain(href)) {
                context.crawlResult().addElement(new Link(depth, href));
                context.scheduler().submitChildTask(href, depth + 1, context.config(), context.latch());
            }
        });
    }

    private boolean isValidDomain(String url) {
        return context.config().getAllowedDomains().stream().anyMatch(url::contains);
    }
}
