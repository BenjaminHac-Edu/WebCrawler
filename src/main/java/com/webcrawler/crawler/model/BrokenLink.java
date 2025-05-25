package com.webcrawler.crawler.model;

public class BrokenLink extends PageElement {
    private final String url;

    public BrokenLink(int depth, int order, String parentUrl, String url) {
        super(depth, order, parentUrl);
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public String toMarkdown(String indent) {
        return "<br>" + indent + " broken link <a>" + url + "</a>";
    }
}
