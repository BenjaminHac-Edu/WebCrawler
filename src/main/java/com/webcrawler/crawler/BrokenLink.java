package com.webcrawler.crawler;

public class BrokenLink extends PageElement {
    private final String url;

    public BrokenLink(int depth, String url) {
        super(depth);
        this.url = url;
    }

    @Override
    public String toMarkdown(String indent) {
        return "<br>" + indent + " broken link <a>" + url + "</a>";
    }
}
