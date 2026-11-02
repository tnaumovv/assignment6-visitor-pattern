package filesystem;

import java.util.List;

public final class Main {
    public static void main(String[] args) {
        if (args.length > 2) {
            throw new IllegalArgumentException("Usage: Main [filename-suffix] [minimum-bytes]");
        }
        String suffix = args.length > 0 ? args[0] : ".txt";
        long minimumSize = args.length > 1 ? Long.parseLong(args[1]) : 100;

        DirectoryNode root = createTree();
        TreeWalker walker = new TreeWalker();
        TotalSizeVisitor total = new TotalSizeVisitor();
        SearchVisitor search = new SearchVisitor(name -> name.endsWith(suffix), minimumSize);
        StatisticsVisitor statistics = new StatisticsVisitor();

        walker.walk(root, total);
        walker.walk(root, search);
        walker.walk(root, statistics);

        System.out.println("Root: " + root.getName());
        System.out.println("Total size: " + total.getTotalSize() + " bytes");
        System.out.println("Search: suffix=" + suffix + ", minimum=" + minimumSize + " bytes");
        System.out.println("Matches: " + search.getMatches());
        System.out.println("Files: " + statistics.getFileCount());
        System.out.println("Directories: " + statistics.getDirectoryCount());
        System.out.println("Maximum depth: " + statistics.getMaxDepth());

        long firstTotal = total.getTotalSize();
        List<String> firstMatches = search.getMatches();
        int firstFiles = statistics.getFileCount();
        int firstDirectories = statistics.getDirectoryCount();
        int firstDepth = statistics.getMaxDepth();
        walker.walk(root, total);
        walker.walk(root, search);
        walker.walk(root, statistics);
        boolean sameResults = firstTotal == total.getTotalSize()
                && firstMatches.equals(search.getMatches())
                && firstFiles == statistics.getFileCount()
                && firstDirectories == statistics.getDirectoryCount()
                && firstDepth == statistics.getMaxDepth();
        System.out.println("Repeated run unchanged: " + sameResults);
    }

    private static DirectoryNode createTree() {
        DirectoryNode root = new DirectoryNode("coursework");
        DirectoryNode docs = new DirectoryNode("docs");
        DirectoryNode drafts = new DirectoryNode("drafts");
        DirectoryNode images = new DirectoryNode("images");
        root.add(new FileNode("readme.txt", 120));
        root.add(docs);
        root.add(images);
        root.add(new DirectoryNode("empty"));
        docs.add(new FileNode("notes.txt", 80));
        docs.add(new FileNode("report.txt", 350));
        docs.add(drafts);
        drafts.add(new FileNode("plan.txt", 100));
        images.add(new FileNode("logo.png", 900));
        return root;
    }
}
