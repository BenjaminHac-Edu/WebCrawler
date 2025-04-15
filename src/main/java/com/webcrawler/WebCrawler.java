package com.webcrawler;


import com.webcrawler.crawler.CrawlResult;
import com.webcrawler.crawler.Crawler;
import com.webcrawler.crawler.MarkdownWriter;

import java.util.List;

public class WebCrawler {
    public static void main(String[] args) {
        if (args.length < 3) {
            System.out.println("Usage: java WebCrawler <URL> <depth> <domains>");
            return;
        }

        String startUrl = args[0];
        int depth = Integer.parseInt(args[1]);
        String[] domains = args[2].split(",");

        Crawler crawler = new Crawler(startUrl, depth, domains);
        CrawlResult result = crawler.startCrawling();

        List<String> markdown = MarkdownWriter.toMarkdown(result);
        MarkdownWriter.saveToMarkdown("report.md", markdown);
    }
}