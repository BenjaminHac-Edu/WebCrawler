package com.webcrawler;


import com.webcrawler.crawler.*;
import com.webcrawler.html.HtmlDocumentFetcher;
import com.webcrawler.html.JsoupHtmlDocumentFetcher;
import com.webcrawler.output.MarkdownWriter;

import java.util.List;

public class WebCrawler {
    public static void main(String[] args) {
        if (args.length < 3) {
            System.out.println("Usage: java WebCrawler <URL> <depth> <domains>");
            return;
        }

        String startUrl = args[0];
        int chosenDepth = Integer.parseInt(args[1]);
        String[] domains = args[2].split(",");

        CrawlerConfig config = new CrawlerConfig(startUrl, chosenDepth, domains);
        HtmlDocumentFetcher fetcher = new JsoupHtmlDocumentFetcher();
        HttpStatusChecker statusChecker = new JsoupHttpStatusChecker();
        Crawler crawler = new Crawler(config, fetcher, statusChecker);
        CrawlResult crawlResult = crawler.startCrawling();

        List<String> markdownText = MarkdownWriter.toMarkdownLines(crawlResult);
        MarkdownWriter.saveToMarkdown("report.md", markdownText);
    }
}