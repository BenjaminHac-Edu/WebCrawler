package com.webcrawler.crawler.model;

public abstract class PageElement implements Comparable<PageElement> {
    private final int depth;
    private final int order;
    private final String parentUrl;

    public PageElement(int depth, int order, String parentUrl) {
        this.depth = depth;
        this.order = order;
        this.parentUrl = parentUrl;
    }

    public int getDepth() {
        return depth;
    }

    public int getOrder() {
        return order;
    }

    public String getParentUrl() {
        return parentUrl;
    }

    @Override
    public int compareTo(PageElement other) {
        return Integer.compare(this.order, other.order);
    }

    public abstract String toMarkdown(String indent);
}
