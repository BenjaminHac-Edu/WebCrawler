package com.webcrawler.html;


import java.util.List;

public interface HtmlDocument {
    List<HtmlElement> selectHeadings();
    List<HtmlElement> selectLinks();
}
