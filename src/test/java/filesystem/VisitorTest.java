package filesystem;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VisitorTest {
    private final TreeWalker walker = new TreeWalker();

    private DirectoryNode sampleTree() {
        DirectoryNode root = new DirectoryNode("campus");
        DirectoryNode docs = new DirectoryNode("docs");
        DirectoryNode drafts = new DirectoryNode("drafts");
        root.add(new FileNode("z.txt", 30));
        root.add(docs);
        root.add(new DirectoryNode("empty"));
        docs.add(new FileNode("b.txt", 20));
        docs.add(new FileNode("a.txt", 10));
        docs.add(new FileNode("image.png", 50));
        docs.add(drafts);
        drafts.add(new FileNode("todo.txt", 5));
        return root;
    }

    @Test
    void doubleDispatchSelectsBothOverloadsExactlyOncePerNode() {
        List<String> calls = new ArrayList<>();
        Visitor recorder = new Visitor() {
            public void reset(AbstractNode root) { calls.clear(); }
            public void visit(FileNode file) { calls.add("file:" + file.getName()); }
            public void visit(DirectoryNode directory) { calls.add("dir:" + directory.getName()); }
        };
        DirectoryNode root = new DirectoryNode("root");
        root.add(new FileNode("one.txt", 1));
        root.add(new DirectoryNode("empty"));
        walker.walk(root, recorder);
        assertEquals(List.of("dir:root", "file:one.txt", "dir:empty"), calls);
    }

    @Test
    void totalSizeIncludesEveryNestedFileOnce() {
        TotalSizeVisitor visitor = new TotalSizeVisitor();
        walker.walk(sampleTree(), visitor);
        assertEquals(115, visitor.getTotalSize());
    }

    @Test
    void searchCombinesFilenameAndInclusiveSize() {
        SearchVisitor visitor = new SearchVisitor(name -> name.endsWith(".txt"), 20);
        walker.walk(sampleTree(), visitor);
        assertEquals(List.of("docs/b.txt", "z.txt"), visitor.getMatches());
    }

    @Test
    void searchSortsRootRelativePathsIndependentlyOfInsertionOrder() {
        SearchVisitor visitor = new SearchVisitor(name -> name.endsWith(".txt"), 0);
        walker.walk(sampleTree(), visitor);
        assertEquals(List.of("docs/a.txt", "docs/b.txt", "docs/drafts/todo.txt", "z.txt"),
                visitor.getMatches());
        assertThrows(UnsupportedOperationException.class, () -> visitor.getMatches().add("fake"));
    }

    @Test
    void selectedNestedRootDoesNotIncludeItsAncestors() {
        DirectoryNode outer = new DirectoryNode("outer");
        DirectoryNode chosen = new DirectoryNode("chosen");
        outer.add(chosen);
        chosen.add(new FileNode("notes.txt", 7));
        SearchVisitor visitor = new SearchVisitor(name -> true, 0);
        walker.walk(chosen, visitor);
        assertEquals(List.of("notes.txt"), visitor.getMatches());
    }

    @Test
    void emptyDirectoryAndNoMatchesReturnZeroAndEmptyList() {
        TotalSizeVisitor total = new TotalSizeVisitor();
        SearchVisitor search = new SearchVisitor(name -> name.endsWith(".pdf"), 0);
        walker.walk(new DirectoryNode("empty"), total);
        assertEquals(0, total.getTotalSize());
        walker.walk(sampleTree(), search);
        assertTrue(search.getMatches().isEmpty());
    }

    @Test
    void repeatedRunsResetSizeAndSearchEvenAfterTreeChanges() {
        DirectoryNode root = new DirectoryNode("root");
        root.add(new FileNode("first.txt", 4));
        TotalSizeVisitor total = new TotalSizeVisitor();
        SearchVisitor search = new SearchVisitor(name -> true, 0);
        walker.walk(root, total);
        walker.walk(root, search);
        root.add(new FileNode("second.txt", 6));
        walker.walk(root, total);
        walker.walk(root, search);
        assertEquals(10, total.getTotalSize());
        assertEquals(List.of("first.txt", "second.txt"), search.getMatches());
        walker.walk(new DirectoryNode("empty"), total);
        walker.walk(new DirectoryNode("empty"), search);
        assertEquals(0, total.getTotalSize());
        assertTrue(search.getMatches().isEmpty());
    }

    @Test
    void fileCanBeTheChosenRootAndZeroThresholdIsInclusive() {
        FileNode file = new FileNode("zero.txt", 0);
        SearchVisitor search = new SearchVisitor(name -> name.equals("zero.txt"), 0);
        TotalSizeVisitor total = new TotalSizeVisitor();
        walker.walk(file, search);
        walker.walk(file, total);
        assertEquals(List.of("zero.txt"), search.getMatches());
        assertEquals(0, total.getTotalSize());
    }

    @Test
    void rejectsInvalidSearchConfiguration() {
        assertThrows(IllegalArgumentException.class, () -> new SearchVisitor(name -> true, -1));
        assertThrows(NullPointerException.class, () -> new SearchVisitor(null, 0));
    }

    @Test
    void totalSizeRejectsOverflowInsteadOfWrappingNegative() {
        DirectoryNode root = new DirectoryNode("root");
        root.add(new FileNode("huge.bin", Long.MAX_VALUE));
        root.add(new FileNode("one.bin", 1));
        TotalSizeVisitor visitor = new TotalSizeVisitor();
        assertThrows(ArithmeticException.class, () -> walker.walk(root, visitor));
        walker.walk(new FileNode("small.bin", 3), visitor);
        assertEquals(3, visitor.getTotalSize());
    }
}
