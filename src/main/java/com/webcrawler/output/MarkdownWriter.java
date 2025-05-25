package com.webcrawler.output;

import com.webcrawler.crawler.core.CrawlResult;
import com.webcrawler.crawler.model.Heading;
import com.webcrawler.crawler.model.PageElement;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

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
        markdownResult.add("");

        // Group elements by root start URL
        Map<String, List<PageElement>> grouped = new LinkedHashMap<>();
        for (String root : result.getRootUrls()) {
            grouped.put(root, new ArrayList<>());
        }

        for (PageElement el : result.getSortedElements()) {
            String root = el.getParentUrl();
            if (root != null) {
                if(!grouped.containsKey(root))
                    grouped.put(root, new ArrayList<>());
                grouped.get(root).add(el);
            }
        }

        // Output grouped results
        for (Map.Entry<String, List<PageElement>> entry : grouped.entrySet()) {
            markdownResult.add("### Results for: <a>" + entry.getKey() + "</a>");

            int lastDepth = -1;
            boolean wasLastElementAHeading = false;

            for (PageElement pageElement : entry.getValue()) {
                int depth = pageElement.getDepth();

                if (depth != lastDepth) {
                    markdownResult.add("<br>depth: " + depth);
                    lastDepth = depth;
                }

                if (!wasLastElementAHeading && pageElement instanceof Heading)
                    wasLastElementAHeading = true;
                else if (wasLastElementAHeading && !(pageElement instanceof Heading)) {
                    markdownResult.add(""); // separate sections
                    wasLastElementAHeading = false;
                }

                markdownResult.add(pageElement.toMarkdown(getIndentation(depth)));
            }

            markdownResult.add(""); // spacing between root results
        }

        return markdownResult;
    }

    private static String getIndentation(int depth) {
        return "--".repeat(depth - 1) + (depth > 1 ? ">" : "");
    }
}
