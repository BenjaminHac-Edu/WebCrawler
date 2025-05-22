package com.webcrawler.crawler;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CrawlResult {
    private final String startUrl;
    private final List<PageElement> elements = new ArrayList<>();
    private final List<String> rootUrls = new ArrayList<>();

    public CrawlResult(String startUrl) {
        this.startUrl = startUrl;
    }

    public void addRootUrl(String url) {
        rootUrls.add(url);
    }

    public List<String> getRootUrls() {
        return rootUrls;
    }

    public void addElement(PageElement element) {
        elements.add(element);
    }

    public String getStartUrl() {
        return startUrl;
    }

    public List<PageElement> getSortedElements() {
        return elements.stream()
                .sorted()
                .collect(Collectors.toList());
    }
}
