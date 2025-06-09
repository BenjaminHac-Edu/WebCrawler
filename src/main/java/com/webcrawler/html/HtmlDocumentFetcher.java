package com.webcrawler.html;

import java.io.IOException;

public interface HtmlDocumentFetcher {
    HtmlDocument fetch(String url) throws IOException;
}
