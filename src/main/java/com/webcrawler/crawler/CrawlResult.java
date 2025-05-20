package com.webcrawler.crawler;

import java.util.ArrayList;
import java.util.List;

public class CrawlResult {
    private final String startUrl;
    private final List<PageElement> elements = new ArrayList<>();

    public CrawlResult(String startUrl) {
        this.startUrl = startUrl;
    }

    public void addElement(PageElement element) {
        elements.add(element);
    }

    public String getStartUrl() {
        return startUrl;
    }

    public List<PageElement> getElements() {
        return elements;
    }
}
