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
        boolean lastElementWasAHeading = false;

        for (PageElement pageElement : result.getElements()) {
            int depth = pageElement.getDepth();

            if (depth != lastDepth) {
                markdown.add("<br>depth: " + depth);
                lastDepth = depth;
            }

            if (!lastElementWasAHeading && pageElement instanceof Heading) {
                lastElementWasAHeading = true;
            } else if (lastElementWasAHeading && !(pageElement instanceof Heading)) {
                markdown.add("");
                lastElementWasAHeading = false;
            }
            markdown.add(pageElement.toMarkdown(getIndentation(depth)));
        }

        return markdown;
    }

    private static String getIndentation(int depth) {
        return "--".repeat(depth - 1) + (depth > 1 ? ">" : "");
    }
}
