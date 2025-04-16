package com.webcrawler.crawler;

public abstract class PageElement {
    protected final int depth;

    public PageElement(int depth) {
        this.depth = depth;
    }

    public int getDepth() {
        return depth;
    }

    public abstract String toMarkdown(String indent);
}
