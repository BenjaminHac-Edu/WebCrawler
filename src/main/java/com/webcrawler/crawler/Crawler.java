package com.webcrawler.crawler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.*;

public class Crawler {
    private final String startUrl;
    private final int maxDepth;
    private final Set<String> allowedDomains;
    private final Set<String> visitedUrls = new HashSet<>();
    private final CrawlResult crawlResult;

    public Crawler(String startUrl, int maxDepth, String[] domains) {
        this.startUrl = startUrl;
        this.maxDepth = maxDepth;
        this.allowedDomains = new HashSet<>(Arrays.asList(domains));
        this.crawlResult = new CrawlResult(startUrl);
    }

    public CrawlResult startCrawling() {
        crawl(startUrl, 1);
        return crawlResult;
    }

    private void crawl(String url, int depth) {
        if (depth > maxDepth || visitedUrls.contains(url)) return;

        visitedUrls.add(url);

        try {
            Document document = fetchDocument(url);

            extractHeadings(document, depth);
            extractLinks(document, depth);
        } catch (IOException e) {
            crawlResult.addElement(new BrokenLink(depth, url));
        }
    }

    private void extractHeadings(Document document, int depth) {
        Elements headings = document.select("h1, h2, h3, h4, h5, h6");
        for (Element heading : headings) {
            crawlResult.addElement(new Heading(depth, heading.text()));
        }
    }

    private void extractLinks(Document document, int depth) {
        Elements links = document.select("a[href]");
        for (Element link : links) {
            String linkHref = link.absUrl("href");
            if (linkHref.isBlank()) continue;

            if (UrlHelper.isBrokenLink(linkHref)) {
                crawlResult.addElement(new BrokenLink(depth, linkHref));
            } else if (isValidDomain(linkHref)) {
                crawlResult.addElement(new Link(depth, linkHref));
                crawl(linkHref, depth + 1);
            }
        }
    }

    // also for overriding to test class
    protected Document fetchDocument(String url) throws IOException {
        return Jsoup.connect(url).get();
    }

    private boolean isValidDomain(String url) {
        return allowedDomains.stream().anyMatch(url::contains);
    }
}
