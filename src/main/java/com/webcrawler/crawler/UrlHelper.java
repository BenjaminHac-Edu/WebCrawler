package com.webcrawler.crawler;

import org.jsoup.Jsoup;

import java.io.IOException;

public class UrlHelper {
    public static boolean isBrokenLink(String url) {
        try {
            int statusCode = Jsoup.connect(url).ignoreHttpErrors(true).execute().statusCode();
            return statusCode >= 400;
        } catch (IOException | IllegalArgumentException e) {
            return true;
        }
    }
}
