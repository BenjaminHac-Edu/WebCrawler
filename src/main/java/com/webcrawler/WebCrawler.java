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
        int choosenDepth = Integer.parseInt(args[1]);
        String[] domains = args[2].split(",");

        Crawler crawler = new Crawler(startUrl, choosenDepth, domains);
        CrawlResult crawlResult = crawler.startCrawling();

        List<String> markdownText = MarkdownWriter.toMarkdownLines(crawlResult);
        MarkdownWriter.saveToMarkdown("report.md", markdownText);
    }
}