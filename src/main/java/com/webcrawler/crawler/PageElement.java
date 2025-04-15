package com.webcrawler.crawler;

public class PageElement {
    public enum ElementType { HEADING, LINK, BROKEN_LINK }

    private final ElementType type;
    private final int depth;
    private final String content;
    private final String url; // only for links

    public PageElement(ElementType type, int depth, String content, String url) {
        this.type = type;
        this.depth = depth;
        this.content = content;
        this.url = url;
    }

    public ElementType getType() {
        return type;
    }

    public int getDepth() {
        return depth;
    }

    public String getContent() {
        return content;
    }

    public String getUrl() {
        return url;
    }
}
