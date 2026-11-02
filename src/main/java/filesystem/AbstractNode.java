package filesystem;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;

public abstract class AbstractNode implements Element {
    private final String name;
    private DirectoryNode parent;

    protected AbstractNode(String name) {
        Objects.requireNonNull(name, "Name cannot be null");
        if (name.isBlank() || name.equals(".") || name.equals("..")
                || name.contains("/") || name.contains("\\")) {
            throw new IllegalArgumentException("Use a non-empty name without path separators");
        }
        this.name = name;
    }

    @Override
    public final String getName() {
        return name;
    }

    public final DirectoryNode getParent() {
        return parent;
    }

    // Only DirectoryNode.add uses this after validating the whole change.
    final void attachTo(DirectoryNode parent) {
        this.parent = parent;
    }

    public final String pathFrom(AbstractNode root) {
        Deque<String> parts = new ArrayDeque<>();
        AbstractNode current = this;
        while (current != root) {
            if (current == null) {
                throw new IllegalArgumentException("The root is not an ancestor");
            }
            parts.addFirst(current.getName());
            current = current.getParent();
        }
        return parts.isEmpty() ? name : String.join("/", parts);
    }

    public final int depthFrom(AbstractNode root) {
        int depth = 0;
        AbstractNode current = this;
        while (current != root) {
            if (current == null) {
                throw new IllegalArgumentException("The root is not an ancestor");
            }
            depth++;
            current = current.getParent();
        }
        return depth;
    }
}
