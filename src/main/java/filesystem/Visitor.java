package filesystem;

public interface Visitor {
    void reset(AbstractNode root);
    void visit(FileNode file);
    void visit(DirectoryNode directory);
}
