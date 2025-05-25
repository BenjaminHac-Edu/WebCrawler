package com.webcrawler.crawler.model;

public class Heading extends PageElement {
    private final int tag;
    private final String content;

    public Heading(int depth, int order, String parentUrl, String tagName, String content) {
        super(depth, order, parentUrl);
        this.tag = Integer.parseInt(tagName.substring(tagName.length() - 1));
        this.content = content;
    }

    @Override
    public String toMarkdown(String indent) {
        return ("#".repeat(tag)) + " " + indent + " " + content;
    }
}
