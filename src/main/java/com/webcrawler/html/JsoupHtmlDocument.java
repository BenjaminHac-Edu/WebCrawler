package com.webcrawler.html;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.List;
import java.util.stream.Collectors;

public class JsoupHtmlDocument implements HtmlDocument {
    private final Document document;
    private final String baseUrl;

    public JsoupHtmlDocument(Document document, String baseUrl) {
        this.document = document;
        this.baseUrl = baseUrl;
    }

    @Override
    public List<HtmlElement> selectHeadings() {
        Elements headings = document.select("h1, h2, h3, h4, h5, h6");
        return headings.stream().map(JsoupHtmlElement::new).collect(Collectors.toList());
    }

    @Override
    public List<HtmlElement> selectLinks() {
        Elements links = document.select("a[href]");
        return links.stream().map(JsoupHtmlElement::new).collect(Collectors.toList());
    }
}
