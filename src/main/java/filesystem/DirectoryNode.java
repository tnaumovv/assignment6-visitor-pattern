package filesystem;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class DirectoryNode extends AbstractNode {
    private final List<AbstractNode> children = new ArrayList<>();

    public DirectoryNode(String name) {
        super(name);
    }

    public void add(AbstractNode child) {
        Objects.requireNonNull(child, "Child cannot be null");
        if (child.getParent() != null) {
            throw new IllegalArgumentException("The child already has a parent");
        }
        for (AbstractNode ancestor = this; ancestor != null; ancestor = ancestor.getParent()) {
            if (ancestor == child) {
                throw new IllegalArgumentException("A directory cannot contain itself or an ancestor");
            }
        }
        for (AbstractNode existing : children) {
            if (existing.getName().equals(child.getName())) {
                throw new IllegalArgumentException("Duplicate child name: " + child.getName());
            }
        }
        children.add(child);
        child.attachTo(this);
    }

    public List<AbstractNode> getChildren() {
        return List.copyOf(children);
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
