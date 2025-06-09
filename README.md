# WebCrawler

A simple Java-based Web Crawler that recursively visits websites and extracts headings and links, saving the results to a Markdown file.

---

## Requirements

- Java 17+
- Maven 3.6+

---

## How to Build

Clone the repository and run:

```bash
mvn clean package
```

This will:
- Compile the source code
- Run all unit tests
- Generate a `.jar` file in the `target/` folder

---

## How to Run the Tests

To run all unit tests:

```bash
mvn test
```

---

## Usage

Run the crawler from the command line with the following arguments:

```bash
java -jar target/webcrawler-jar-with-dependencies.jar <start-url1>,<start-url2>... <depth> <domain1>,<domain2> ...
```

### Example:

```bash
java -jar target/webcrawler-jar-with-dependencies.jar https://google.com,https://example.com 2 google.com,example.com,iana.org
```

Output will be saved to:

```
report.md
```

## Additional information
The number of threads that should run while crawling the website can be adjusted in the WebCrawler (Main) Class, currently the value is set to:
```java
public static final int numberOfAllowedThreads = 8;
```
