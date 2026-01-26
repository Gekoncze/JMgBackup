package cz.mg.backup.gui.services;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.backup.entities.Algorithm;
import cz.mg.backup.entities.Directory;
import cz.mg.backup.entities.Node;
import cz.mg.backup.gui.MainWindow;
import cz.mg.backup.gui.dialogs.ProgressDialog;
import cz.mg.backup.gui.views.directory.DirectoryTreeEntry;
import cz.mg.backup.gui.views.directory.DirectoryView;
import cz.mg.backup.services.ChecksumActions;
import cz.mg.backup.services.DirectoryManager;
import cz.mg.backup.services.DirectorySearch;
import cz.mg.collections.list.List;

import java.nio.file.Path;

public @Service class DirectoryTreeActions {
    public static final String RELOAD_TITLE = "Reload";

    private static volatile @Service DirectoryTreeActions instance;

    public static @Service DirectoryTreeActions getInstance() {
        if (instance == null) {
            synchronized (Service.class) {
                if (instance == null) {
                    instance = new DirectoryTreeActions();
                    instance.directoryManager = DirectoryManager.getInstance();
                    instance.directorySearch = DirectorySearch.getInstance();
                    instance.directoryTreeFactory = DirectoryTreeFactory.getInstance();
                    instance.checksumActions = ChecksumActions.getInstance();
                    instance.fileManager = FileManager.getInstance();
                }
            }
        }
        return instance;
    }

    private @Service DirectoryManager directoryManager;
    private @Service DirectorySearch directorySearch;
    private @Service DirectoryTreeFactory directoryTreeFactory;
    private @Service ChecksumActions checksumActions;
    private @Service FileManager fileManager;

    private DirectoryTreeActions() {
    }

    public void reload(@Mandatory MainWindow window) {
        Path detailsPath = window.getDetailsView().getPath();
        Directory left = window.getLeftView().getDirectory();
        Directory right = window.getRightView().getDirectory();
        DirectoryTreeEntry[] roots = new DirectoryTreeEntry[2];
        Node[] detailsNode = new Node[1];

        // process can be cancelled at any time, so keep that in mind to ensure some level of consistency
        boolean completed = ProgressDialog.run(
            window,
            RELOAD_TITLE,
            progress -> {
                if (left != null) directoryManager.reload(left, progress);
                if (right != null) directoryManager.reload(right, progress);
                directoryManager.compare(left, right, progress);
                roots[0] = directoryTreeFactory.create(left, progress);
                roots[1] = directoryTreeFactory.create(right, progress);
                detailsNode[0] = directorySearch.find(left, right, detailsPath, progress);
            }
        );

        // ui may be outdated, but should be acceptable for now
        if (completed) {
            window.getLeftView().setRoot(roots[0]);
            window.getRightView().setRoot(roots[1]);
            window.getDetailsView().setNode(detailsNode[0]);
        }
    }

    public void reload(@Mandatory MainWindow window, @Mandatory DirectoryView view) {
        Path detailsPath = window.getDetailsView().getPath();
        Directory directory = view.getDirectory();
        Directory left = window.getLeftView().getDirectory();
        Directory right = window.getRightView().getDirectory();
        DirectoryTreeEntry[] roots = new DirectoryTreeEntry[2];
        Node[] detailsNode = new Node[1];

        // process can be cancelled at any time, so keep that in mind to ensure some level of consistency
        boolean complete = ProgressDialog.run(
            window,
            RELOAD_TITLE,
            progress -> {
                if (directory != null) directoryManager.reload(directory, progress);
                directoryManager.compare(left, right, progress);
                roots[0] = directoryTreeFactory.create(left, progress);
                roots[1] = directoryTreeFactory.create(right, progress);
                detailsNode[0] = directorySearch.find(left, right, detailsPath, progress);
            }
        );

        // ui may be outdated, but should be acceptable for now
        if (complete) {
            window.getLeftView().setRoot(roots[0]);
            window.getRightView().setRoot(roots[1]);
            window.getDetailsView().setNode(detailsNode[0]);
        }
    }

    public void load(@Mandatory MainWindow window, @Mandatory DirectoryView view) {
        Path directoryPath = view.getSelectionView().getPath();
        Path detailsPath = window.getDetailsView().getPath();
        int directoryIndex = view == window.getLeftView() ? 0 : 1;
        Directory[] directories = new Directory[]{
            window.getLeftView().getDirectory(),
            window.getRightView().getDirectory()
        };
        DirectoryTreeEntry[] roots = new DirectoryTreeEntry[2];
        Node[] detailsNode = new Node[1];

        // process can be cancelled at any time, so keep that in mind to ensure some level of consistency
        boolean complete = ProgressDialog.run(
            window,
            RELOAD_TITLE,
            progress -> {
                directories[directoryIndex] = directoryManager.load(directoryPath, progress);
                directoryManager.compare(directories[0], directories[1], progress);
                roots[0] = directoryTreeFactory.create(directories[0], progress);
                roots[1] = directoryTreeFactory.create(directories[1], progress);
                detailsNode[0] = directorySearch.find(directories[0], directories[1], detailsPath, progress);
            }
        );

        // ui may be outdated, but should be acceptable for now
        if (complete) {
            window.getLeftView().setRoot(roots[0]);
            window.getRightView().setRoot(roots[1]);
            window.getDetailsView().setNode(detailsNode[0]);
        }
    }

    public void computeChecksum(@Mandatory MainWindow window, @Mandatory DirectoryView view) {
        Path detailsPath = window.getDetailsView().getPath();
        List<Node> nodes = view.getSelectedNodes();
        Algorithm algorithm = window.getSettings().getAlgorithm();
        Directory left = window.getLeftView().getDirectory();
        Directory right = window.getRightView().getDirectory();
        DirectoryTreeEntry[] roots = new DirectoryTreeEntry[2];
        Node[] detailsNode = new Node[1];

        // process can be cancelled at any time, so keep that in mind to ensure some level of consistency
        boolean complete = ProgressDialog.run(
            window,
            "Compute checksum",
            progress -> {
                checksumActions.compute(nodes, algorithm, progress);
                directoryManager.compare(left, right, progress);
                roots[0] = directoryTreeFactory.create(left, progress);
                roots[1] = directoryTreeFactory.create(right, progress);
                detailsNode[0] = directorySearch.find(left, right, detailsPath, progress);
            }
        );

        // ui may be outdated, but that is acceptable here
        if (complete) {
            window.getLeftView().setRoot(roots[0]);
            window.getRightView().setRoot(roots[1]);
            window.getDetailsView().setNode(detailsNode[0]);
        }
    }

    public void clearChecksum(@Mandatory MainWindow window, @Mandatory DirectoryView view) {
        Path detailsPath = window.getDetailsView().getPath();
        List<Node> nodes = view.getSelectedNodes();
        Directory left = window.getLeftView().getDirectory();
        Directory right = window.getRightView().getDirectory();
        DirectoryTreeEntry[] roots = new DirectoryTreeEntry[2];
        Node[] detailsNode = new Node[1];

        // process can be cancelled at any time, so keep that in mind to ensure some level of consistency
        boolean complete = ProgressDialog.run(
            window,
            "Clear checksum",
            progress -> {
                checksumActions.clear(nodes, progress);
                directoryManager.compare(left, right, progress);
                roots[0] = directoryTreeFactory.create(left, progress);
                roots[1] = directoryTreeFactory.create(right, progress);
                detailsNode[0] = directorySearch.find(left, right, detailsPath, progress);
            }
        );

        // ui may be outdated, but that is acceptable here
        if (complete) {
            window.getLeftView().setRoot(roots[0]);
            window.getRightView().setRoot(roots[1]);
            window.getDetailsView().setNode(detailsNode[0]);
        }
    }

    public void openInFileManager(@Mandatory DirectoryView view) {
        Node node = view.getPopupNode();
        if (node != null) {
            fileManager.open(node.getPath());
        }
    }
}
