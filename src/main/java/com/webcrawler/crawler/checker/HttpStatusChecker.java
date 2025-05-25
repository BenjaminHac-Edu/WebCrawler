package com.webcrawler.crawler.checker;

import java.io.IOException;

public interface HttpStatusChecker {
    boolean isBroken(String url) throws IOException, IllegalArgumentException;
}
