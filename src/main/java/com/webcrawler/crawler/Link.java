package com.webcrawler.crawler;

public class Link extends PageElement{
    private final String url;

    public Link(int depth, String url) {
        super(depth);
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
