package com.webcrawler.crawler;

public interface HttpStatusChecker {
    boolean isBroken(String url);
}
