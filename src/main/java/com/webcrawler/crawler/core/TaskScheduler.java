package com.webcrawler.crawler.core;

import com.webcrawler.crawler.config.CrawlerConfig;

public interface TaskScheduler {
    void submitTask(String url, int depth, CrawlerConfig config);
}
