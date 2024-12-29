package cz.mg.backup.gui.views.directory;

import cz.mg.annotations.classes.Component;
import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.backup.components.Progress;
import cz.mg.backup.entities.Algorithm;
import cz.mg.backup.entities.Checksum;
import cz.mg.backup.entities.Directory;
import cz.mg.backup.entities.Node;
import cz.mg.backup.gui.MainWindow;
import cz.mg.backup.gui.dialogs.ProgressDialog;
import cz.mg.backup.gui.event.UserActionListener;
import cz.mg.backup.gui.event.UserMouseClickListener;
import cz.mg.backup.gui.event.UserTreeSelectionListener;
import cz.mg.backup.gui.services.ButtonFactory;
import cz.mg.backup.gui.services.DirectoryTreeFactory;
import cz.mg.backup.gui.services.SelectionSimplifier;
import cz.mg.backup.services.*;
import cz.mg.collections.list.List;
import cz.mg.collections.map.Map;
import cz.mg.collections.pair.Pair;
import cz.mg.panel.Panel;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.TreePath;
import java.awt.event.MouseEvent;
import java.io.File;
import java.nio.file.Path;
import java.util.Date;

public @Component class DirectoryView extends Panel {
    private static final int MARGIN = 4;
    private static final int PADDING = 4;

    private final @Service DirectoryReader directoryReader = DirectoryReader.getInstance();
    private final @Service DirectorySearch directorySearch = DirectorySearch.getInstance();
    private final @Service StatisticsCounter statisticsCounter = StatisticsCounter.getInstance();
    private final @Service DirectoryTreeFactory directoryTreeFactory = DirectoryTreeFactory.getInstance();
    private final @Service ButtonFactory buttonFactory = ButtonFactory.getInstance();
    private final @Service ChecksumService checksumService = ChecksumService.getInstance();
    private final @Service SelectionSimplifier selectionSimplifier = SelectionSimplifier.getInstance();

    private final @Mandatory MainWindow window;
    private final @Mandatory JTextField pathField;
    private final @Mandatory JTree tree;
    private final @Mandatory JFileChooser directoryChooser;
    private final @Mandatory JPopupMenu popupMenu;

    private @Optional Path path;
    private @Optional Directory directory;

    public DirectoryView(@Mandatory MainWindow window) {
        this.window = window;
        setMargin(MARGIN);
        setPadding(PADDING);

        pathField = new JTextField();
        pathField.setEditable(false);

        Panel pathPanel = new Panel(0, PADDING);
        pathPanel.addHorizontal(pathField, 1, 0);
        pathPanel.addHorizontal(buttonFactory.create("...", this::select));

        tree = new JTree();
        tree.setBorder(BorderFactory.createEtchedBorder());

        addVertical(pathPanel, 1, 0);
        addVertical(new JScrollPane(tree), 1, 1);

        directoryChooser = new JFileChooser();
        directoryChooser.setDialogType(JFileChooser.OPEN_DIALOG);
        directoryChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

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

    public @Optional Path getPath() {
        return path;
    }

    public void setPath(@Optional Path path) {
        this.path = path;
        pathField.setText(path == null ? "" : path.toString());
    }

    public @Optional Directory getDirectory() {
        return directory;
    }

    public void setDirectory(@Optional Directory directory) {
        this.directory = directory;
        refresh();
    }

    private void select() {
        int result = directoryChooser.showOpenDialog(this);
        File file = directoryChooser.getSelectedFile();
        if (file != null && result == JFileChooser.APPROVE_OPTION) {
            setPath(file.toPath());
        }
        reload();
    }

    public void reload() {
        Path displayedPath = getDisplayedPath();

        if (path != null) {
            Map<Path, Pair<Checksum, Date>> checksums = ProgressDialog.compute(
                window,
                "Collect checksums",
                null,
                progress -> checksumService.collect(directory, progress)
            );

            setDirectory(
                ProgressDialog.compute(
                    window,
                    "Load directory",
                    "Load directory " + path,
                    progress -> directoryReader.read(path, progress)
                )
            );

            ProgressDialog.run(
                window,
                "Gather statistics",
                null,
                progress -> statisticsCounter.count(directory, progress)
            );

            ProgressDialog.run(
                window,
                "Restore checksums",
                null,
                progress -> checksumService.restore(directory, checksums, progress)
            );
        } else {
            setDirectory(null);
        }

        window.compare();

        restoreDisplayedPath(displayedPath);
    }

    public void refresh() {
        if (directory != null) {
            List<TreePath> expandedPaths = TreeUtils.getExpandedPaths(tree);
            TreePath collapsedRootPath = TreeUtils.getCollapsedRootPath(tree);
            List<TreePath> selectedPaths = TreeUtils.getSelectedPaths(tree);

            tree.setModel(new ObjectTreeModel(
                directoryTreeFactory.create(directory, new Progress("Build directory tree")))
            );

            TreeUtils.expandPaths(tree, expandedPaths);
            TreeUtils.collapseRootPath(tree, collapsedRootPath);
            TreeUtils.selectPaths(tree, selectedPaths);
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
