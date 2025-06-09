package com.webcrawler.crawler.model;

public class Link extends PageElement {
    private final String url;

    public Link(int depth, int order, String parentUrl, String url) {
        super(depth, order, parentUrl);
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public String toMarkdown(String indent) {
        return "<br>" + indent + " link to <a>" + url + "</a>";
    }
}
