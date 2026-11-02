package filesystem;

public final class TotalSizeVisitor implements Visitor {
    private long totalSize;

    @Override
    public void reset(AbstractNode root) {
        totalSize = 0;
    }

    @Override
    public void visit(FileNode file) {
        totalSize = Math.addExact(totalSize, file.getSize());
    }

    @Override
    public void visit(DirectoryNode directory) {
        // Directories have no byte size of their own.
    }

    public long getTotalSize() {
        return totalSize;
    }
}
