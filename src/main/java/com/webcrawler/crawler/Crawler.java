package com.webcrawler.crawler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.*;

public class Crawler {
    private final String startUrl;
    private final int maxDepth;
    private final Set<String> allowedDomains;
    private final Set<String> visitedUrls = new HashSet<>();
    private final List<String> outputLines = new ArrayList<>();

    public Crawler(String startUrl, int maxDepth, String[] domains) {
        this.startUrl = startUrl;
        this.maxDepth = maxDepth;
        this.allowedDomains = new HashSet<>(Arrays.asList(domains));
    }

    public void startCrawling() {
        outputLines.add("input: <a>" + startUrl + "</a>");
        crawl(startUrl, 1);
        MarkdownWriter.saveToMarkdown("report.md", outputLines);
    }

    private void crawl(String url, int depth) {
        if (depth > maxDepth || visitedUrls.contains(url)) return;

        visitedUrls.add(url);

        String indent = "-->".repeat(depth - 1);
        outputLines.add("<br>depth: " + depth);

        try {
            Document document = Jsoup.connect(url).get();
            System.out.println("Crawling: " + url);

            // Extract headings
            Elements headings = document.select("h1, h2, h3, h4, h5, h6");
            for (Element heading : headings) {
                System.out.println("  ".repeat(depth) + "- " + heading.text());
                String prefix = "#".repeat(Integer.parseInt(heading.tagName().substring(1)));
                outputLines.add(prefix + " " + indent + " " + heading.text());
            }

            outputLines.add("");

            // Extract links
            Elements links = document.select("a[href]");
            for (Element link : links) {
                String linkHref = link.absUrl("href");
                if (linkHref.isBlank()) continue;

                if (UrlHelper.isBrokenLink(linkHref)) {
                    outputLines.add("<br>" + indent + " broken link <a>" + linkHref + "</a>");
                } else if (isValidDomain(linkHref)) {
                    outputLines.add("<br>" + indent + " link to <a>" + linkHref + "</a>");
                    crawl(linkHref, depth + 1);
                }
            }

            outputLines.add("<br>");

        } catch (IOException e) {
            System.out.println("Broken link: " + url);
            outputLines.add("<br>" + indent + " broken link <a>" + url + "</a>");
        }
    }

    private boolean isValidDomain(String url) {
        return allowedDomains.stream().anyMatch(url::contains);
    }
}
