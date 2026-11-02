package filesystem;

public final class StatisticsVisitor implements Visitor {
    private AbstractNode root;
    private int fileCount;
    private int directoryCount;
    private int maxDepth;

    @Override
    public void reset(AbstractNode root) {
        this.root = root;
        fileCount = 0;
        directoryCount = 0;
        maxDepth = 0;
    }

    @Override
    public void visit(FileNode file) {
        fileCount++;
        updateDepth(file);
    }

    @Override
    public void visit(DirectoryNode directory) {
        directoryCount++;
        updateDepth(directory);
    }

    private void updateDepth(AbstractNode node) {
        maxDepth = Math.max(maxDepth, node.depthFrom(root));
    }

    public int getFileCount() {
        return fileCount;
    }

    public int getDirectoryCount() {
        return directoryCount;
    }

    public int getMaxDepth() {
        return maxDepth;
    }
}
