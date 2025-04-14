package com.webcrawler.crawler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Crawler {
    private final String startUrl;
    private final int maxDepth;
    private final Set<String> allowedDomains;
    private final Set<String> visitedUrls = new HashSet<>();

    public Crawler(String startUrl, int maxDepth, String[] domains) {
        this.startUrl = startUrl;
        this.maxDepth = maxDepth;
        this.allowedDomains = new HashSet<>(Arrays.asList(domains));
    }

    public void startCrawling() {
        crawl(startUrl, 0);
    }

    private void crawl(String url, int depth) {
        if (depth > maxDepth) return;

        visitedUrls.add(url);
        try {
            Document document = Jsoup.connect(url).get();
            System.out.println("Crawling: " + url);

            // Extract headings
            Elements headings = document.select("h1, h2, h3, h4, h5, h6");
            for (Element heading : headings) {
                System.out.println("  ".repeat(depth) + "- " + heading.text());
            }

            // Extract links
            Elements links = document.select("a[href]");
            for (Element link : links) {
                String linkHref = link.absUrl("href");
                if (isValidUrl(linkHref)) {
                    crawl(linkHref, depth + 1);
                }
            }

        } catch (IOException e) {
            System.out.println("Broken link: " + url);
        }
    }

    private boolean isValidUrl(String url) {
        return allowedDomains.stream().anyMatch(url::contains) && !visitedUrls.contains(url);
    }
}
