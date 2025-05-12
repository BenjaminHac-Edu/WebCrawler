package com.webcrawler.crawler;

import org.jsoup.Jsoup;

import java.io.IOException;

public class JsoupHttpStatusChecker implements HttpStatusChecker {
    @Override
    public boolean isBroken(String url) {
        try {
            int statusCode = Jsoup.connect(url).ignoreHttpErrors(true).execute().statusCode();
            return statusCode >= 400;
        } catch (IOException | IllegalArgumentException e) {
            return true;
        }
    }
}
