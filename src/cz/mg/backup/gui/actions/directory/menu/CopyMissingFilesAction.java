package cz.mg.backup.gui.actions.directory.menu;

import cz.mg.annotations.classes.Component;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.backup.components.Progress;
import cz.mg.backup.entities.Algorithm;
import cz.mg.backup.entities.Directory;
import cz.mg.backup.entities.File;
import cz.mg.backup.entities.Node;
import cz.mg.backup.gui.MainWindow;
import cz.mg.backup.gui.actions.Action;
import cz.mg.backup.gui.dialogs.ProgressDialog;
import cz.mg.backup.gui.entities.State;
import cz.mg.backup.gui.services.RefreshService;
import cz.mg.backup.gui.views.directory.DirectoryTreeView;
import cz.mg.backup.services.FileBackup;
import cz.mg.collections.list.List;

import javax.swing.*;
import java.util.function.Function;

public @Component class CopyMissingFilesAction implements Action {
    private final @Mandatory FileBackup fileBackup = FileBackup.getInstance();
    private final @Mandatory RefreshService refreshService = RefreshService.getInstance();

    private final @Mandatory MainWindow window;
    private final @Mandatory DirectoryTreeView source;

    public CopyMissingFilesAction(@Mandatory MainWindow window, @Mandatory DirectoryTreeView source) {
        this.window = window;
        this.source = source;
    }

    @Override
    public @Mandatory String getName() {
        return "Copy missing files";
    }

    @Override
    public @Optional Character getMnemonic() {
        return null;
    }

    @Override
    public @Optional KeyStroke getShortcut() {
        return null;
    }

    @Override
    public @Optional Icon getSmallIcon() {
        return null;
    }

    @Override
    public @Optional Icon getLargeIcon() {
        return null;
    }

    @Override
    public void run() {
        State state = window.getApplicationState();
        List<Node> nodes = source.getSelectedNodes();
        Algorithm algorithm = window.getSettings().getAlgorithm();
        DirectoryTreeView target = window.getLeftView() == source ? window.getRightView() : window.getLeftView();

        List<File> files = collectMissingFiles(nodes);

        if (files != null) {
            if (!files.isEmpty()) {
                int choice = JOptionPane.showConfirmDialog(
                    window,
                    "Total of " + files.count() + " files of total size " + size(files) + " will be copied.",
                    getName(),
                    JOptionPane.OK_CANCEL_OPTION
                );

                if (choice == JOptionPane.OK_OPTION) {
                    if (source.getRoot() != null && target.getRoot() != null) {
                        copyMissingFiles(files, source.getRoot(), target.getRoot(), algorithm, state);
                        window.setApplicationState(state);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(
                    window,
                    getName(),
                    "No missing files to copy.",
                    JOptionPane.INFORMATION_MESSAGE
                );
            }
        }
    }

    private @Optional List<File> collectMissingFiles(@Mandatory List<Node> nodes) {
        return ProgressDialog.compute(
            window,
            "Collect missing files",
            (Function<Progress, List<File>>) progress -> fileBackup.collectMissingFiles(nodes, progress)
        ).getResult();
    }

    private void copyMissingFiles(
        @Mandatory List<File> files,
        @Mandatory Directory source,
        @Mandatory Directory target,
        @Mandatory Algorithm algorithm,
        @Mandatory State state
    ) {
        ProgressDialog.run(
            window,
            getName(),
            progress -> {
                fileBackup.copyMissingFiles(files, source, target, algorithm, progress);
                refreshService.refresh(state, progress);
            }
        );
    }

    private long size(@Mandatory List<File> files) {
        long size = 0;
        for (File file : files) {
            size += file.getProperties().getSize();
        }
        return size;
    }
}
