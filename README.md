# Assignment 6 - Visitor Pattern

An in-memory file tree with total size, filename/size search and statistics visitors.

Requires **JDK 17 or newer** and **Maven 3.8+**. Source is compiled for Java 17.
Run these commands from the project root:

```bash
mvn test
mvn compile exec:java
```

Change the search suffix and inclusive minimum byte size:

```bash
mvn compile exec:java -Dexec.args=".png 500"
```

The default search is case-sensitive: filenames ending in `.txt`, size >= 100 bytes.
The API also accepts any `Predicate<String>` over the filename.

```text
Root: coursework
Total size: 1550 bytes
Search: suffix=.txt, minimum=100 bytes
Matches: [docs/drafts/plan.txt, docs/report.txt, readme.txt]
Files: 5
Directories: 5
Maximum depth: 3
Repeated run unchanged: true
```

`TreeWalker` owns traversal and resets the visitor before every run. Use
`walker.walk(root, visitor)` for each operation. Visitors are reusable for sequential
runs; they do not traverse children themselves. A search path excludes the selected
directory root's name; a root file uses its own name. Root depth is zero. Search
results are sorted lexicographically and use `/`. Children and result lists cannot
be edited through getters. Do not change the tree during a walk.

Tests: `TreeIntegrityTest`, `VisitorTest`, `StatisticsVisitorTest`.

[PDF report](report/Assignment6_Visitor_Report.pdf) ·
[Report text](report.md) ·
[UML](report/Visitor_Class_Diagram.svg) ·
[Editable PlantUML](report/Visitor_Class_Diagram.puml)
