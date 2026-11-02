package filesystem;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StatisticsVisitorTest {
    private final TreeWalker walker = new TreeWalker();

    @Test
    void countsFilesDirectoriesAndDeepestFile() {
        DirectoryNode root = new DirectoryNode("root");
        DirectoryNode docs = new DirectoryNode("docs");
        DirectoryNode drafts = new DirectoryNode("drafts");
        root.add(new FileNode("readme.txt", 5));
        root.add(docs);
        root.add(new DirectoryNode("empty"));
        docs.add(new FileNode("report.pdf", 10));
        docs.add(drafts);
        drafts.add(new FileNode("plan.txt", 2));
        StatisticsVisitor visitor = new StatisticsVisitor();
        walker.walk(root, visitor);
        assertEquals(3, visitor.getFileCount());
        assertEquals(4, visitor.getDirectoryCount());
        assertEquals(3, visitor.getMaxDepth());
    }

    @Test
    void emptyDirectoryCountsTheRootAtDepthZero() {
        StatisticsVisitor visitor = new StatisticsVisitor();
        walker.walk(new DirectoryNode("empty"), visitor);
        assertEquals(0, visitor.getFileCount());
        assertEquals(1, visitor.getDirectoryCount());
        assertEquals(0, visitor.getMaxDepth());
    }

    @Test
    void rootFileHasNoDirectoriesAndDepthZero() {
        StatisticsVisitor visitor = new StatisticsVisitor();
        walker.walk(new FileNode("single.txt", 7), visitor);
        assertEquals(1, visitor.getFileCount());
        assertEquals(0, visitor.getDirectoryCount());
        assertEquals(0, visitor.getMaxDepth());
    }

    @Test
    void repeatedRunsResetAllCountersAndCountEmptyDirectoryDepth() {
        StatisticsVisitor visitor = new StatisticsVisitor();
        DirectoryNode root = new DirectoryNode("root");
        DirectoryNode child = new DirectoryNode("child");
        root.add(child);
        walker.walk(root, visitor);
        child.add(new DirectoryNode("deepEmpty"));
        walker.walk(root, visitor);
        assertEquals(0, visitor.getFileCount());
        assertEquals(3, visitor.getDirectoryCount());
        assertEquals(2, visitor.getMaxDepth());
        walker.walk(new FileNode("one.txt", 1), visitor);
        assertEquals(1, visitor.getFileCount());
        assertEquals(0, visitor.getDirectoryCount());
        assertEquals(0, visitor.getMaxDepth());
    }

    @Test
    void depthStartsAtChosenSubtreeRatherThanTopmostAncestor() {
        DirectoryNode outer = new DirectoryNode("outer");
        DirectoryNode chosen = new DirectoryNode("chosen");
        outer.add(chosen);
        chosen.add(new FileNode("inside.txt", 1));
        StatisticsVisitor visitor = new StatisticsVisitor();
        walker.walk(chosen, visitor);
        assertEquals(1, visitor.getFileCount());
        assertEquals(1, visitor.getDirectoryCount());
        assertEquals(1, visitor.getMaxDepth());
    }
}
