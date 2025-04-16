package com.webcrawler.crawler;

public class Heading extends PageElement{
    private final String content;

    public Heading(int depth, String content) {
        super(depth);
        this.content = content;
    }

    @Override
    public String toMarkdown(String indent) {
        return "# " + indent + " " + content;
    }
}
