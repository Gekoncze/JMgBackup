package cz.mg.backup.gui.views.directory;

import cz.mg.annotations.classes.Component;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.backup.entities.Directory;
import cz.mg.backup.entities.Node;
import cz.mg.backup.gui.MainWindow;
import cz.mg.backup.gui.actions.directory.menu.ClearChecksumAction;
import cz.mg.backup.gui.actions.directory.menu.ComputeChecksumAction;
import cz.mg.backup.gui.actions.directory.menu.OpenFileManagerAction;
import cz.mg.backup.gui.components.ActionItem;
import cz.mg.backup.gui.entities.Side;
import cz.mg.backup.gui.event.*;
import cz.mg.backup.gui.views.directory.wrapper.AbstractTreeNode;
import cz.mg.collections.list.List;
import cz.mg.panel.Panel;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.TreePath;
import java.awt.event.MouseEvent;
import java.nio.file.Path;

public @Component class DirectoryTreeView extends Panel {
    private static final int MARGIN = 4;
    private static final int PADDING = 4;

    private final @Mandatory MainWindow window;
    private final @Mandatory Side side;
    private final @Mandatory PathSelectionView selectionView;
    private final @Mandatory DirectoryTree tree;
    private final @Mandatory JPopupMenu popupMenu;

    private @Optional TreePath popupMenuPath;

    public DirectoryTreeView(@Mandatory MainWindow window, @Mandatory Side side) {
        this.window = window;
        this.side = side;
        setMargin(MARGIN);
        setPadding(PADDING);

        selectionView = new PathSelectionView(window, this);
        addVertical(selectionView, 1, 0);

        tree = new DirectoryTree();
        tree.setTransferHandler(new UserDragAndDropListener(this::onFileDropped));
        tree.addMouseListener(new UserMouseClickListener(MouseEvent.BUTTON1, this::updateDetails));
        tree.addMouseListener(new UserMouseClickListener(MouseEvent.BUTTON3, this::showPopupMenu));
        tree.addTreeSelectionListener(new UserTreeSelectionListener(this::onSelectionChanged));
        addVertical(new JScrollPane(tree), 1, 1);

        popupMenu = new JPopupMenu();
        popupMenu.add(new ActionItem(new ComputeChecksumAction(window, this)));
        popupMenu.add(new ActionItem(new ClearChecksumAction(window, this)));
        popupMenu.add(new ActionItem(new OpenFileManagerAction(this)));
    }

    public @Mandatory Side getSide() {
        return side;
    }

    public @Mandatory PathSelectionView getSelectionView() {
        return selectionView;
    }

    public @Optional Directory getRoot() {
        return tree.getRoot();
    }

    public void setRoot(@Optional Directory root) {
        tree.setRoot(root);
        popupMenuPath = null;
    }

    private void onFileDropped(@Mandatory Path path) {
        selectionView.setPath(path);
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

    private void updateDetails(@Mandatory MouseEvent event) {
        Node node = getNode(tree.getPathForLocation(event.getX(), event.getY()));
        if (node != null) {
            window.getDetailsView().setNode(node);
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
        return path == null ? null : ((AbstractTreeNode) path.getLastPathComponent()).getNode();
    }
}
