package com.webcrawler.html;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class JsoupHtmlDocumentFetcher implements HtmlDocumentFetcher {
    @Override
    public HtmlDocument fetch(String url) throws IOException {
        Document doc = Jsoup.connect(url).get();
        return new JsoupHtmlDocument(doc, url);
    }
}
