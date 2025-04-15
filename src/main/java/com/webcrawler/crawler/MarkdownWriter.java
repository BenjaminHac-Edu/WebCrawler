package com.webcrawler.crawler;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MarkdownWriter {
    public static void saveToMarkdown(String filename, List<String> lines) {
        try (FileWriter writer = new FileWriter(filename)) {
            for (String line : lines) {
                writer.write(line + "\n");
            }
            System.out.println("Report saved to " + filename);
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }

    public static List<String> toMarkdown(CrawlResult result) {
        List<String> markdown = new ArrayList<>();
        markdown.add("input: <a>" + result.getStartUrl() + "</a>");

        int lastDepth = -1;

        for (PageElement el : result.getElements()) {
            String indent = "-->".repeat(el.getDepth() - 1);

            if (el.getDepth() != lastDepth) {
                markdown.add("<br>depth: " + el.getDepth());
                lastDepth = el.getDepth();
            }

            switch (el.getType()) {
                case HEADING -> markdown.add("# " + indent + " " + el.getContent());
                case LINK -> markdown.add("<br>" + indent + " link to <a>" + el.getUrl() + "</a>");
                case BROKEN_LINK -> markdown.add("<br>" + indent + " broken link <a>" + el.getUrl() + "</a>");
            }
        }

        markdown.add("<br>");
        return markdown;
    }
}
