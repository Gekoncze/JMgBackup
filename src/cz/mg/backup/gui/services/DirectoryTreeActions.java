package cz.mg.backup.gui.services;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.backup.entities.Algorithm;
import cz.mg.backup.entities.Directory;
import cz.mg.backup.entities.Node;
import cz.mg.backup.gui.entities.State;
import cz.mg.backup.gui.MainWindow;
import cz.mg.backup.gui.dialogs.ProgressDialog;
import cz.mg.backup.gui.entities.Side;
import cz.mg.backup.gui.views.directory.DirectoryTreeView;
import cz.mg.backup.services.ChecksumManager;
import cz.mg.backup.services.DirectoryManager;
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
                    instance.fileManager = FileManager.getInstance();
                    instance.checksumManager = ChecksumManager.getInstance();
                    instance.stateService = StateService.getInstance();
                }
            }
        }
        return instance;
    }

    private @Service DirectoryManager directoryManager;
    private @Service FileManager fileManager;
    private @Service ChecksumManager checksumManager;
    private @Service StateService stateService;

    private DirectoryTreeActions() {
    }

    public void reload(@Mandatory MainWindow window) {
        State state = window.getApplicationState();

        // process can be cancelled at any time, so keep that in mind to ensure some level of consistency
        ProgressDialog.run(
            window,
            RELOAD_TITLE,
            progress -> {
                directoryManager.reload(state.getLeft(), progress);
                directoryManager.reload(state.getRight(), progress);
                stateService.refresh(state, progress);
            }
        );

        window.setApplicationState(state);
    }

    public void reload(@Mandatory MainWindow window, @Mandatory DirectoryTreeView view) {
        State state = window.getApplicationState();
        Directory directory = view.getRoot();

        // process can be cancelled at any time, so keep that in mind to ensure some level of consistency
        ProgressDialog.run(
            window,
            RELOAD_TITLE,
            progress -> {
                directoryManager.reload(directory, progress);
                stateService.refresh(state, progress);
            }
        );

        window.setApplicationState(state);
    }

    public void load(@Mandatory MainWindow window, @Mandatory DirectoryTreeView view) {
        State state = window.getApplicationState();
        Path path = view.getSelectionView().getPath();
        Side side = view.getSide();

        // process can be cancelled at any time, so keep that in mind to ensure some level of consistency
        ProgressDialog.run(
            window,
            RELOAD_TITLE,
            progress -> {
                state.setDirectory(directoryManager.load(path, progress), side);
                stateService.refresh(state, progress);
            }
        );

        window.setApplicationState(state);
    }

    public void computeChecksum(@Mandatory MainWindow window, @Mandatory DirectoryTreeView view) {
        State state = window.getApplicationState();
        List<Node> nodes = view.getSelectedNodes();
        Algorithm algorithm = window.getSettings().getAlgorithm();

        // process can be cancelled at any time, so keep that in mind to ensure some level of consistency
        ProgressDialog.run(
            window,
            "Compute checksum",
            progress -> {
                checksumManager.compute(nodes, algorithm, progress);
                stateService.refresh(state, progress);
            }
        );

        window.setApplicationState(state);
    }

    public void clearChecksum(@Mandatory MainWindow window, @Mandatory DirectoryTreeView view) {
        State state = window.getApplicationState();
        List<Node> nodes = view.getSelectedNodes();

        // process can be cancelled at any time, so keep that in mind to ensure some level of consistency
        ProgressDialog.run(
            window,
            "Clear checksum",
            progress -> {
                checksumManager.clear(nodes, progress);
                stateService.refresh(state, progress);
            }
        );

        window.setApplicationState(state);
    }

    public void openInFileManager(@Mandatory DirectoryTreeView view) {
        Node node = view.getPopupNode();
        if (node != null) {
            fileManager.open(node.getPath());
        }
    }
}
