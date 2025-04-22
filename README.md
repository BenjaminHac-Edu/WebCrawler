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
java -jar target/webcrawler-jar-with-dependencies.jar <start-url> <depth> <domain1>,<domain2> ...
```

### Example:

```bash
java -jar target/webcrawler-jar-with-dependencies.jar https://example.com 2 example.com,anotherdomain.com
```

Output will be saved to:

```
report.md
```
