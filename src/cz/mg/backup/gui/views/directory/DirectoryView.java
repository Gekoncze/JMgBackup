package cz.mg.backup.gui.views.directory;

import cz.mg.annotations.classes.Component;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.backup.entities.Directory;
import cz.mg.backup.entities.Node;
import cz.mg.backup.gui.MainWindow;
import cz.mg.backup.gui.event.*;
import cz.mg.backup.gui.services.DirectoryTreeActions;
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

    private final @Mandatory DirectoryTreeActions actions = DirectoryTreeActions.getInstance();

    private final @Mandatory MainWindow window;
    private final @Mandatory PathSelectionView selectionView;
    private final @Mandatory DirectoryTree tree;
    private final @Mandatory JPopupMenu popupMenu;

    private @Optional TreePath popupMenuPath;

    public DirectoryView(@Mandatory MainWindow window) {
        this.window = window;
        setMargin(MARGIN);
        setPadding(PADDING);

        selectionView = new PathSelectionView();
        selectionView.addChangeListener(new UserPathChangeListener(this::onPathChanged));
        selectionView.addReloadListener(new UserButtonListener(this::onReloadClicked));
        addVertical(selectionView, 1, 0);

        tree = new DirectoryTree();
        tree.setTransferHandler(new UserDragAndDropListener(this::onFileDropped));
        tree.addMouseListener(new UserMouseClickListener(this::onMouseClicked));
        tree.addTreeSelectionListener(new UserTreeSelectionListener(this::onSelectionChanged));
        addVertical(new JScrollPane(tree), 1, 1);

        popupMenu = new JPopupMenu();

        JMenuItem computeChecksumMenuItem = new JMenuItem("Compute checksum");
        computeChecksumMenuItem.addActionListener(new UserActionListener(this::onComputeChecksumClicked));
        popupMenu.add(computeChecksumMenuItem);

        JMenuItem clearChecksumMenuItem = new JMenuItem("Clear checksum");
        clearChecksumMenuItem.addActionListener(new UserActionListener(this::onClearChecksumClicked));
        popupMenu.add(clearChecksumMenuItem);

        JMenuItem openInFileManagerMenuItem = new JMenuItem("Open in file manager");
        openInFileManagerMenuItem.addActionListener(new UserActionListener(this::onOpenInFileManagerClicked));
        popupMenu.add(openInFileManagerMenuItem);
    }

    public @Mandatory PathSelectionView getSelectionView() {
        return selectionView;
    }

    public @Optional Directory getDirectory() {
        DirectoryTreeEntry entry = tree.getRoot();
        return entry == null ? null : (Directory) entry.get();
    }

    public void setRoot(@Optional DirectoryTreeEntry root) {
        List<TreePath> expandedPaths = TreeUtils.getExpandedPaths(tree);
        TreePath collapsedRootPath = TreeUtils.getCollapsedRootPath(tree);
        List<TreePath> selectedPaths = TreeUtils.getSelectedPaths(tree);

        tree.setRoot(root);

        TreeUtils.expandPaths(tree, expandedPaths);
        TreeUtils.collapseRootPath(tree, collapsedRootPath);
        TreeUtils.selectPaths(tree, selectedPaths);

        popupMenuPath = null;
    }

    private void onPathChanged() {
        actions.load(window, this);
    }

    private void onReloadClicked() {
        actions.reload(window, this);
    }

    private void onComputeChecksumClicked() {
        actions.computeChecksum(window, this);
    }

    private void onClearChecksumClicked() {
        actions.clearChecksum(window, this);
    }

    private void onOpenInFileManagerClicked() {
        actions.openInFileManager(this);
    }

    private void onFileDropped(@Mandatory Path path) {
        selectionView.setPath(path);
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
        popupMenuPath = TreeUtils.getSelectedPathAt(tree, event);
        if (popupMenuPath != null) {
            popupMenu.show(tree, event.getX(), event.getY());
        }
    }

    public @Mandatory List<Node> getSelectedNodes() {
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

    public @Optional Node getPopupNode() {
        if (popupMenuPath != null) {
            return getNode(popupMenuPath);
        }
        return null;
    }

    private @Optional Node getNode(@Optional TreePath path) {
        return path == null ? null : ((DirectoryTreeEntry) path.getLastPathComponent()).get();
    }
}
