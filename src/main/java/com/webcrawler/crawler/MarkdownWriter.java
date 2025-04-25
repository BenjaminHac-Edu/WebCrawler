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

    public static List<String> toMarkdownLines(CrawlResult result) {
        List<String> markdownResult = new ArrayList<>();
        markdownResult.add("input: <a>" + result.getStartUrl() + "</a>");

        int lastDepth = -1;
        boolean wasLastElementAHeading = false;

        for (PageElement pageElement : result.getElements()) {
            int depth = pageElement.getDepth();

            if (depth != lastDepth) {
                markdownResult.add("<br>depth: " + depth);
                lastDepth = depth;
            }

            if (!wasLastElementAHeading && pageElement instanceof Heading)
                wasLastElementAHeading = true;
            else if (wasLastElementAHeading && !(pageElement instanceof Heading)) {
                markdownResult.add(""); // empty line between headings and links (cleaner Format)
                wasLastElementAHeading = false;
            }

            markdownResult.add(pageElement.toMarkdown(getIndentation(depth)));
        }

        return markdownResult;
    }

    private static String getIndentation(int depth) {
        return "--".repeat(depth - 1) + (depth > 1 ? ">" : "");
    }
}
