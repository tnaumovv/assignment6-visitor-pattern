package filesystem;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Objects;

public final class TreeWalker implements Visitor {
    private final Deque<AbstractNode> pending = new ArrayDeque<>();
    private Visitor operation;

    public void walk(AbstractNode root, Visitor visitor) {
        Objects.requireNonNull(root, "Root cannot be null");
        Objects.requireNonNull(visitor, "Visitor cannot be null");
        if (visitor == this || operation != null) {
            throw new IllegalArgumentException("Use a separate operation and sequential walks");
        }
        operation = visitor;
        try {
            operation.reset(root);
            pending.push(root);
            while (!pending.isEmpty()) {
                pending.pop().accept(this);
            }
        } finally {
            pending.clear();
            operation = null;
        }
    }

    @Override
    public void reset(AbstractNode root) {
        pending.clear();
    }

    @Override
    public void visit(FileNode file) {
        operation.visit(file);
    }

    @Override
    public void visit(DirectoryNode directory) {
        operation.visit(directory);
        List<AbstractNode> children = directory.getChildren();
        // Reverse pushing preserves the directory's insertion order in depth-first traversal.
        for (int i = children.size() - 1; i >= 0; i--) {
            pending.push(children.get(i));
        }
    }
}
