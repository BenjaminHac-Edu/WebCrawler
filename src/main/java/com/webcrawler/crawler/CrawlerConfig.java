package com.webcrawler.crawler;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class CrawlerConfig {
    private final String startUrl;
    private final int maxDepth;
    private final Set<String> allowedDomains;

    public CrawlerConfig(String startUrl, int maxDepth, String[] allowedDomains) {
        this.startUrl = startUrl;
        this.maxDepth = maxDepth;
        this.allowedDomains = new HashSet<>(Arrays.asList(allowedDomains));
    }

    public String getStartUrl() {
        return startUrl;
    }

    public int getMaxDepth() {
        return maxDepth;
    }

    public Set<String> getAllowedDomains() {
        return allowedDomains;
    }
}
