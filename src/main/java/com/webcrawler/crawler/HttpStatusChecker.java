package com.webcrawler.crawler;

import java.io.IOException;

public interface HttpStatusChecker {
    boolean isBroken(String url) throws IOException, IllegalArgumentException;
}
