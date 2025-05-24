package com.webcrawler;


import com.webcrawler.crawler.*;
import com.webcrawler.html.JsoupHtmlDocumentFetcher;
import com.webcrawler.output.MarkdownWriter;

import java.util.ArrayList;
import java.util.List;

public class WebCrawler {
    public static final int numberOfAllowedThreads = 8;

    public static void main(String[] args) {
        if (args.length < 3) {
            System.out.println("Usage: java WebCrawler <url1,url2,...> <depth> <domain1,domain2,...>");
            return;
        }

        String[] urls = args[0].split(",");
        int chosenDepth = Integer.parseInt(args[1]);
        String[] domains = args[2].split(",");

        List<CrawlerConfig> configs = new ArrayList<>();
        for (String url : urls) {
            configs.add(new CrawlerConfig(url, chosenDepth, domains));
        }

        ConcurrentCrawler concurrentCrawler = new ConcurrentCrawler(new JsoupHtmlDocumentFetcher(), new JsoupHttpStatusChecker(), numberOfAllowedThreads);
        CrawlResult result = concurrentCrawler.start(configs);
        List<String> markdownText = MarkdownWriter.toMarkdownLines(result);
        MarkdownWriter.saveToMarkdown("report.md", markdownText);


        /*HtmlDocumentFetcher fetcher = new JsoupHtmlDocumentFetcher();
        HttpStatusChecker statusChecker = new JsoupHttpStatusChecker();
        Crawler crawler = new Crawler(config, fetcher, statusChecker);
        CrawlResult crawlResult = crawler.startCrawling();

        List<String> markdownText = MarkdownWriter.toMarkdownLines(crawlResult);
        MarkdownWriter.saveToMarkdown("report.md", markdownText);*/


        System.out.println("Crawling completed. See report.md");
    }
}