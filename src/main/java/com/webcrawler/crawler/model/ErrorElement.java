package com.webcrawler.crawler.model;

public class ErrorElement extends PageElement {
    private final String message;

    public ErrorElement(int depth, int order, String parentUrl, String message) {
        super(depth, order, parentUrl);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toMarkdown(String indent) {
        return "<br>" + indent + "  Error: " + message;
    }
}
