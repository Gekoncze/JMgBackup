package cz.mg.backup.gui.views.directory;

import cz.mg.annotations.classes.Component;
import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.backup.entities.Algorithm;
import cz.mg.backup.entities.Directory;
import cz.mg.backup.entities.Node;
import cz.mg.backup.gui.MainWindow;
import cz.mg.backup.gui.dialogs.ProgressDialog;
import cz.mg.backup.gui.event.*;
import cz.mg.backup.gui.services.DirectoryTreeFactory;
import cz.mg.backup.gui.services.FileManager;
import cz.mg.backup.services.*;
import cz.mg.collections.list.List;
import cz.mg.panel.Panel;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.TreePath;
import java.awt.event.MouseEvent;
import java.nio.file.Path;

public @Component class DirectoryView extends Panel {
    private static final int MARGIN = 4;
    private static final int PADDING = 4;

    private final @Service DirectorySearch directorySearch = DirectorySearch.getInstance();
    private final @Service DirectoryTreeFactory directoryTreeFactory = DirectoryTreeFactory.getInstance();
    private final @Service ChecksumActions checksumActions = ChecksumActions.getInstance();
    private final @Service DirectoryManager directoryManager = DirectoryManager.getInstance();
    private final @Service FileManager fileManager = FileManager.getInstance();

    private final @Mandatory MainWindow window;
    private final @Mandatory PathSelector pathSelector;
    private final @Mandatory JTree tree;
    private final @Mandatory JPopupMenu popupMenu;

    private @Optional TreePath popupMenuRow;
    private @Optional Directory directory;

    public DirectoryView(@Mandatory MainWindow window) {
        this.window = window;
        setMargin(MARGIN);
        setPadding(PADDING);

        pathSelector = new PathSelector();
        pathSelector.addPathSelectionListener(new UserPathChangeListener(this::reload));
        addVertical(pathSelector, 1, 0);

        tree = new JTree();
        tree.setBorder(BorderFactory.createEtchedBorder());
        tree.setTransferHandler(new UserDragAndDropListener(this::onFileDropped));
        addVertical(new JScrollPane(tree), 1, 1);

        popupMenu = new JPopupMenu();

        JMenuItem computeChecksumMenuItem = new JMenuItem("Compute checksum");
        computeChecksumMenuItem.addActionListener(new UserActionListener(this::computeChecksum));
        popupMenu.add(computeChecksumMenuItem);

        JMenuItem clearChecksumMenuItem = new JMenuItem("Clear checksum");
        clearChecksumMenuItem.addActionListener(new UserActionListener(this::clearChecksum));
        popupMenu.add(clearChecksumMenuItem);

        JMenuItem openInFileManagerMenuItem = new JMenuItem("Open in file manager");
        openInFileManagerMenuItem.addActionListener(new UserActionListener(this::openInFileManager));
        popupMenu.add(openInFileManagerMenuItem);

        tree.addMouseListener(new UserMouseClickListener(this::onMouseClicked));
        tree.addTreeSelectionListener(new UserTreeSelectionListener(this::onSelectionChanged));

        refresh();
    }

    public @Optional Directory getDirectory() {
        return directory;
    }

    public void setDirectory(@Optional Directory directory) {
        this.directory = directory;
        refresh();
    }

    private void onFileDropped(@Mandatory Path path) {
        pathSelector.setPath(path);
    }

    public void reload() {
        Path path = pathSelector.getPath();
        if (path != null) {
            setDirectory(
                ProgressDialog.compute(
                    window,
                    "Reload directory",
                    progress -> directoryManager.reload(directory, path, progress)
                )
            );
        } else {
            setDirectory(null);
        }

        window.compare();
    }

    public void refresh() {
        if (directory != null) {
            Path displayedPath = getDisplayedPath();
            List<TreePath> expandedPaths = TreeUtils.getExpandedPaths(tree);
            TreePath collapsedRootPath = TreeUtils.getCollapsedRootPath(tree);
            List<TreePath> selectedPaths = TreeUtils.getSelectedPaths(tree);

            tree.setModel(new DirectoryTreeModel(
                ProgressDialog.compute(
                    window,
                    "Build directory tree",
                    progress -> directoryTreeFactory.create(directory, progress))
                )
            );

            TreeUtils.expandPaths(tree, expandedPaths);
            TreeUtils.collapseRootPath(tree, collapsedRootPath);
            TreeUtils.selectPaths(tree, selectedPaths);
            restoreDisplayedPath(displayedPath);
        } else {
            tree.setModel(new DirectoryTreeModel(null));
        }
        tree.setCellRenderer(new DirectoryTreeCellRenderer());
        popupMenuRow = null;
    }

    private @Optional Path getDisplayedPath() {
        Node node = window.getDetailsView().getNode();
        if (node != null && directory != null && node.getPath().startsWith(directory.getPath())) {
            return node.getPath();
        } else {
            return null;
        }
    }

    private void restoreDisplayedPath(@Optional Path path) {
        if (path != null && directory != null) {
            window.getDetailsView().setNode(
                ProgressDialog.compute(
                    window,
                    "Search",
                    progress -> directorySearch.find(directory, path, progress)
                )
            );
        }
    }

    private void onMouseClicked(@Mandatory MouseEvent event) {
        if (event.getButton() == MouseEvent.BUTTON1) {
            TreePath path = tree.getPathForLocation(event.getX(), event.getY());
            Node node = getNode(path);
            if (node != null) {
                window.getDetailsView().setNode(node);
            }
        }

        if (event.getButton() == MouseEvent.BUTTON3) {
            showPopupMenu(event);
        }
    }

    private void onSelectionChanged(@Mandatory TreeSelectionEvent event) {
        for (TreePath path : event.getPaths()) {
            if (event.isAddedPath(path)) {
                Node node = getNode(path);
                if (node != null) {
                    window.getDetailsView().setNode(node);
                }
                break;
            }
        }
    }

    private void showPopupMenu(@Mandatory MouseEvent event) {
        popupMenuRow = getSelectedRowAt(event);
        if (popupMenuRow != null) {
            popupMenu.show(tree, event.getX(), event.getY());
        }
    }

    private @Optional TreePath getSelectedRowAt(@Mandatory MouseEvent event) {
        int row = tree.getRowForLocation(event.getX(), event.getY());
        int[] selectedRows = tree.getSelectionRows();
        if (row != -1 && selectedRows != null) {
            for (int selectedRow : selectedRows) {
                if (selectedRow == row) {
                    return tree.getPathForRow(row);
                }
            }
        }
        return null;
    }

    private void computeChecksum() {
        List<Node> nodes = getSelectedNodes();
        Algorithm algorithm = window.getSettings().getAlgorithm();

        ProgressDialog.run(
            window,
            "Compute checksum",
            progress -> checksumActions.compute(nodes, algorithm, progress)
        );

        window.compare();
    }

    private void clearChecksum() {
        List<Node> nodes = getSelectedNodes();

        ProgressDialog.run(
            window,
            "Clear checksum",
            progress -> checksumActions.clear(nodes, progress)
        );

        window.compare();
    }

    private void openInFileManager() {
        if (popupMenuRow != null) {
            Node node = getNode(popupMenuRow);
            if (node != null) {
                fileManager.open(node.getPath());
            }
        }
    }

    private @Mandatory List<Node> getSelectedNodes() {
        List<Node> selectedNodes = new List<>();
        TreePath[] paths = tree.getSelectionPaths();
        if (paths != null) {
            for (TreePath path : paths) {
                Node node = getNode(path);
                if (node != null) {
                    selectedNodes.addLast(node);
                }
            }
        }
        return selectedNodes;
    }

    private @Optional Node getNode(@Optional TreePath path) {
        return path == null ? null : ((DirectoryTreeEntry) path.getLastPathComponent()).get();
    }
}
