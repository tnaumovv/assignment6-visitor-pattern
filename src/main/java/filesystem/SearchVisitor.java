package filesystem;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

public final class SearchVisitor implements Visitor {
    private final Predicate<String> filenameCriterion;
    private final long minimumSize;
    private final List<String> matches = new ArrayList<>();
    private AbstractNode root;

    public SearchVisitor(Predicate<String> filenameCriterion, long minimumSize) {
        this.filenameCriterion = Objects.requireNonNull(filenameCriterion, "Criterion cannot be null");
        if (minimumSize < 0) {
            throw new IllegalArgumentException("Minimum size cannot be negative");
        }
        this.minimumSize = minimumSize;
    }

    @Override
    public void reset(AbstractNode root) {
        this.root = root;
        matches.clear();
    }

    @Override
    public void visit(FileNode file) {
        if (filenameCriterion.test(file.getName()) && file.getSize() >= minimumSize) {
            matches.add(file.pathFrom(root));
        }
    }

    @Override
    public void visit(DirectoryNode directory) {
        // Only files are returned by this search.
    }

    public List<String> getMatches() {
        return matches.stream().sorted().toList();
    }
}
