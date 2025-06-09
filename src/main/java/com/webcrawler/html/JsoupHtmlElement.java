package com.webcrawler.html;

import org.jsoup.nodes.Element;

public class JsoupHtmlElement implements HtmlElement {
    private final Element element;

    public JsoupHtmlElement(Element element) {
        this.element = element;
    }

    @Override
    public String getTagName() {
        return element.tagName();
    }

    @Override
    public String getText() {
        return element.text();
    }

    @Override
    public String getAbsoluteHref() {
        return element.absUrl("href");
    }
}
