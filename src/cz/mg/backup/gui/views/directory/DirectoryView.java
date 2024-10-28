package cz.mg.backup.gui.views.directory;

import cz.mg.annotations.classes.Component;
import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.backup.entities.Directory;
import cz.mg.backup.entities.Node;
import cz.mg.backup.gui.MainWindow;
import cz.mg.backup.gui.dialogs.ProgressDialog;
import cz.mg.backup.gui.event.UserActionListener;
import cz.mg.backup.gui.event.UserMouseClickListener;
import cz.mg.backup.gui.event.UserTreeSelectionListener;
import cz.mg.backup.gui.services.ButtonFactory;
import cz.mg.backup.gui.services.DirectoryTreeFactory;
import cz.mg.backup.services.ChecksumService;
import cz.mg.backup.services.DirectoryReader;
import cz.mg.collections.list.List;
import cz.mg.panel.Panel;

import javax.swing.*;
import javax.swing.tree.TreePath;
import java.awt.event.MouseEvent;
import java.io.File;
import java.nio.file.Path;

public @Component class DirectoryView extends Panel {
    private static final int MARGIN = 4;
    private static final int PADDING = 4;

    private final @Service DirectoryReader directoryReader = DirectoryReader.getInstance();
    private final @Service DirectoryTreeFactory directoryTreeFactory = DirectoryTreeFactory.getInstance();
    private final @Service ButtonFactory buttonFactory = ButtonFactory.getInstance();
    private final @Service ChecksumService checksumService = ChecksumService.getInstance();

    private final @Mandatory MainWindow window;
    private final @Mandatory JTextField pathField;
    private final @Mandatory JTree treeView;
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

        treeView = new JTree();
        treeView.setBorder(BorderFactory.createEtchedBorder());
        treeView.addTreeSelectionListener(new UserTreeSelectionListener(this::onItemSelected));

        addVertical(pathPanel, 1, 0);
        addVertical(new JScrollPane(treeView), 1, 1);

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

        treeView.addMouseListener(new UserMouseClickListener(this::onMouseClicked));

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

    private void onItemSelected() {
        window.getDetailsView().setNode(getSelectedNode());
    }

    private @Optional Node getSelectedNode() {
        if (treeView.getSelectionPath() != null) {
            ObjectTreeEntry entry = (ObjectTreeEntry) treeView.getSelectionPath().getPathComponent(
                treeView.getSelectionPath().getPathCount() - 1
            );

            return (Node) entry.get();
        }

        return null;
    }

    private void select() {
        directoryChooser.showOpenDialog(this);
        File file = directoryChooser.getSelectedFile();
        if (file != null) {
            setPath(file.toPath());
        }
        reload();
    }

    public void reload() {
        if (path != null) {
            setDirectory(
                ProgressDialog.show(
                    window,
                    "Load Directory",
                    () -> directoryReader.read(path, window.getSettings())
                )
            );
        } else {
            setDirectory(null);
        }

        window.compare();
        window.getDetailsView().setNode(null);
    }

    public void refresh() {
        if (directory != null) {
            System.out.println("REFRESH BEGIN");
            List<TreePath> expandedPaths = TreeUtils.getExpandedPaths(treeView);
            List<TreePath> selectedPaths = TreeUtils.getSelectedPaths(treeView);
            treeView.setModel(new ObjectTreeModel(directoryTreeFactory.create(directory)));
            restoreExpandedPaths(expandedPaths);
            restoreSelectedPaths(selectedPaths);
            System.out.println("REFRESH END " + expandedPaths.count() + " " + selectedPaths.count());
        } else {
            treeView.setModel(new ObjectTreeModel(null));
        }
        treeView.setCellRenderer(new NodeCellRenderer());
    }

    private void restoreExpandedPaths(@Mandatory List<TreePath> expandedPaths) {
        for (TreePath expandedPath : expandedPaths) {
            treeView.expandPath(expandedPath);
        }
    }

    private void restoreSelectedPaths(@Mandatory List<TreePath> selectedPaths) {
        for (TreePath selectedPath : selectedPaths) {
            treeView.addSelectionPath(selectedPath);
        }
    }

    private void onMouseClicked(@Mandatory MouseEvent event) {
        if (event.getButton() == MouseEvent.BUTTON3) {
            if (isRowSelectedAt(event)) {
                popupMenu.show(treeView, event.getX(), event.getY());
            }
        }
    }

    private boolean isRowSelectedAt(@Mandatory MouseEvent event) {
        int row = treeView.getRowForLocation(event.getX(), event.getY());
        int[] selectedRows = treeView.getSelectionRows();
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
        ProgressDialog.show(window, "Compute checksum", () -> {
            for (Node node : getSelectedNodes()) {
                checksumService.compute(node, window.getSettings().getAlgorithm());
            }
        });
        window.compare();
    }

    private void clearChecksum() {
        ProgressDialog.show(window, "Clear checksum", () -> {
            for (Node node : getSelectedNodes()) {
                checksumService.clear(node);
            }
        });
        window.compare();
    }

    private List<Node> getSelectedNodes() {
        List<Node> selectedNodes = new List<>();
        TreePath[] paths = treeView.getSelectionPaths();
        if (paths != null) {
            for (TreePath path : paths) {
                Node node = (Node) ((ObjectTreeEntry) path.getLastPathComponent()).get();
                selectedNodes.addLast(node);
            }
        }
        return selectedNodes;
    }
}
