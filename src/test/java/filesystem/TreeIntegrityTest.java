package filesystem;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TreeIntegrityTest {
    @Test
    void rejectsNegativeSizeButAcceptsZero() {
        assertThrows(IllegalArgumentException.class, () -> new FileNode("bad.txt", -1));
        assertEquals(0, new FileNode("empty.txt", 0).getSize());
    }

    @Test
    void rejectsNamesThatWouldMakePathsAmbiguous() {
        for (String name : new String[]{"", " ", ".", "..", "a/b", "a\\b"}) {
            assertThrows(IllegalArgumentException.class, () -> new FileNode(name, 1));
            assertThrows(IllegalArgumentException.class, () -> new DirectoryNode(name));
        }
        assertThrows(NullPointerException.class, () -> new DirectoryNode(null));
    }

    @Test
    void rejectsDuplicateNamesAcrossFileAndDirectory() {
        DirectoryNode root = new DirectoryNode("root");
        root.add(new FileNode("notes", 10));
        DirectoryNode duplicate = new DirectoryNode("notes");
        assertThrows(IllegalArgumentException.class, () -> root.add(duplicate));
        assertNull(duplicate.getParent());
        assertEquals(1, root.getChildren().size());
    }

    @Test
    void rejectsMultipleParentsWithoutChangingTheTree() {
        DirectoryNode first = new DirectoryNode("first");
        DirectoryNode second = new DirectoryNode("second");
        FileNode file = new FileNode("shared.txt", 4);
        first.add(file);
        assertThrows(IllegalArgumentException.class, () -> second.add(file));
        assertSame(first, file.getParent());
        assertTrue(second.getChildren().isEmpty());
    }

    @Test
    void rejectsSelfAndAncestorCycles() {
        DirectoryNode root = new DirectoryNode("root");
        DirectoryNode child = new DirectoryNode("child");
        root.add(child);
        assertThrows(IllegalArgumentException.class, () -> root.add(root));
        assertThrows(IllegalArgumentException.class, () -> child.add(root));
        assertNull(root.getParent());
        assertTrue(child.getChildren().isEmpty());
    }

    @Test
    void childCollectionCannotBeModifiedAndNullIsRejected() {
        DirectoryNode root = new DirectoryNode("root");
        assertThrows(UnsupportedOperationException.class,
                () -> root.getChildren().add(new FileNode("hidden.txt", 1)));
        assertThrows(NullPointerException.class, () -> root.add(null));
        assertTrue(root.getChildren().isEmpty());
    }

    @Test
    void supportsDeepValidNestingWithoutRecursiveTraversal() {
        DirectoryNode root = new DirectoryNode("root");
        DirectoryNode current = root;
        for (int i = 0; i < 1500; i++) {
            DirectoryNode next = new DirectoryNode("level" + i);
            current.add(next);
            current = next;
        }
        current.add(new FileNode("end.txt", 9));
        TotalSizeVisitor visitor = new TotalSizeVisitor();
        new TreeWalker().walk(root, visitor);
        assertEquals(9, visitor.getTotalSize());
    }
}
