package cz.mg.backup.gui.views.directory;

import cz.mg.annotations.classes.Component;
import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.backup.components.Progress;
import cz.mg.backup.entities.Algorithm;
import cz.mg.backup.entities.Directory;
import cz.mg.backup.entities.Node;
import cz.mg.backup.gui.MainWindow;
import cz.mg.backup.gui.dialogs.ProgressDialog;
import cz.mg.backup.gui.event.*;
import cz.mg.backup.gui.services.DirectoryTreeFactory;
import cz.mg.backup.gui.services.SelectionSimplifier;
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
    private final @Service ChecksumService checksumService = ChecksumService.getInstance();
    private final @Service SelectionSimplifier selectionSimplifier = SelectionSimplifier.getInstance();
    private final @Service DirectoryManager directoryManager = DirectoryManager.getInstance();

    private final @Mandatory MainWindow window;
    private final @Mandatory PathSelector pathSelector;
    private final @Mandatory JTree tree;
    private final @Mandatory JPopupMenu popupMenu;

    private @Optional Directory directory;

    public DirectoryView(@Mandatory MainWindow window) {
        this.window = window;
        setMargin(MARGIN);
        setPadding(PADDING);

        pathSelector = new PathSelector(window);
        pathSelector.addPathSelectionListener(new UserPathChangeListener(this::onPathSelected));
        addVertical(pathSelector, 1, 0);

        tree = new JTree();
        tree.setBorder(BorderFactory.createEtchedBorder());
        tree.setTransferHandler(new UserFileDragAndDrop(this::onFileDropped));
        addVertical(new JScrollPane(tree), 1, 1);

        popupMenu = new JPopupMenu();

        JMenuItem computeChecksumMenuItem = new JMenuItem("Compute checksum");
        computeChecksumMenuItem.addActionListener(new UserActionListener(this::computeChecksum));
        popupMenu.add(computeChecksumMenuItem);

        JMenuItem clearChecksumMenuItem = new JMenuItem("Clear checksum");
        clearChecksumMenuItem.addActionListener(new UserActionListener(this::clearChecksum));
        popupMenu.add(clearChecksumMenuItem);

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

    private void onPathSelected(@Optional Path path) {
        reload();
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
                    null,
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

            tree.setModel(new ObjectTreeModel(
                directoryTreeFactory.create(directory, new Progress("Build directory tree")))
            );

            TreeUtils.expandPaths(tree, expandedPaths);
            TreeUtils.collapseRootPath(tree, collapsedRootPath);
            TreeUtils.selectPaths(tree, selectedPaths);
            restoreDisplayedPath(displayedPath);
        } else {
            tree.setModel(new ObjectTreeModel(null));
        }
        tree.setCellRenderer(new NodeCellRenderer());
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
                    null,
                    progress -> directorySearch.find(directory, path, progress)
                )
            );
        }
    }

    private void onMouseClicked(@Mandatory MouseEvent event) {
        if (event.getButton() == MouseEvent.BUTTON1) {
            TreePath path = tree.getPathForLocation(event.getX(), event.getY());
            Node node = getNodeFrom(path);
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
                Node node = getNodeFrom(path);
                if (node != null) {
                    window.getDetailsView().setNode(node);
                }
                break;
            }
        }
    }

    private void showPopupMenu(@Mandatory MouseEvent event) {
        if (isRowSelectedAt(event)) {
            popupMenu.show(tree, event.getX(), event.getY());
        }
    }

    private boolean isRowSelectedAt(@Mandatory MouseEvent event) {
        int row = tree.getRowForLocation(event.getX(), event.getY());
        int[] selectedRows = tree.getSelectionRows();
        if (row != -1 && selectedRows != null) {
            for (int selectedRow : selectedRows) {
                if (selectedRow == row) {
                    return true;
                }
            }
        }
        return false;
    }

    private void computeChecksum() {
        List<Node> nodes = getSimplifiedSelectedNodes();
        Algorithm algorithm = window.getSettings().getAlgorithm();

        ProgressDialog.run(
            window,
            "Compute checksum",
            null,
            progress -> checksumService.compute(nodes, algorithm, progress)
        );

        window.compare();
    }

    private void clearChecksum() {
        List<Node> nodes = getSimplifiedSelectedNodes();

        ProgressDialog.run(
            window,
            "Clear checksum",
            null,
            progress -> checksumService.clear(nodes, progress)
        );

        window.compare();
    }

    private @Mandatory List<Node> getSelectedNodes() {
        List<Node> selectedNodes = new List<>();
        TreePath[] paths = tree.getSelectionPaths();
        if (paths != null) {
            for (TreePath path : paths) {
                Node node = getNodeFrom(path);
                if (node != null) {
                    selectedNodes.addLast(node);
                }
            }
        }
        return selectedNodes;
    }

    private @Mandatory List<Node> getSimplifiedSelectedNodes() {
        return ProgressDialog.compute(
            window,
            "Simplify selection",
            null,
            progress -> selectionSimplifier.simplify(getSelectedNodes(), progress)
        );
    }

    private @Optional Node getNodeFrom(@Optional TreePath path) {
        return path == null ? null : (Node) ((ObjectTreeEntry) path.getLastPathComponent()).get();
    }
}
