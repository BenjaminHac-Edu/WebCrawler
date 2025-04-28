package com.webcrawler.crawler;

public class Heading extends PageElement{
    private final int tag;
    private final String content;

    public Heading(int depth, String tagName, String content) {
        super(depth);
        this.tag = Integer.parseInt(tagName.substring(tagName.length() - 1));
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    @Override
    public String toMarkdown(String indent) {
        return ("#".repeat(tag)) + " " + indent + " " + content;
    }
}
