package cz.mg.backup.gui.actions.directory.menu;

import cz.mg.annotations.classes.Component;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.backup.components.Task;
import cz.mg.backup.entities.Directory;
import cz.mg.backup.entities.Node;
import cz.mg.backup.gui.MainWindow;
import cz.mg.backup.gui.actions.Action;
import cz.mg.backup.gui.dialogs.CreateMissingDirectoriesDialog;
import cz.mg.backup.gui.dialogs.ProgressDialog;
import cz.mg.backup.gui.entities.State;
import cz.mg.backup.gui.services.RefreshService;
import cz.mg.backup.gui.views.directory.DirectoryTreeView;
import cz.mg.backup.services.BackupService;
import cz.mg.collections.list.List;

import javax.swing.*;

public @Component class CreateMissingDirectoriesAction implements Action {
    private final @Mandatory BackupService backupService = BackupService.getInstance();
    private final @Mandatory RefreshService refreshService = RefreshService.getInstance();

    private final @Mandatory MainWindow window;
    private final @Mandatory DirectoryTreeView source;

    public CreateMissingDirectoriesAction(@Mandatory MainWindow window, @Mandatory DirectoryTreeView source) {
        this.window = window;
        this.source = source;
    }

    @Override
    public @Mandatory String getName() {
        return "Create missing directories";
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
        DirectoryTreeView target = window.getLeftView() == source ? window.getRightView() : window.getLeftView();

        if (source.getRoot() != null && target.getRoot() != null) {
            List<Directory> directories = collectMissingDirectories(nodes);
            if (directories != null) {
                if (!directories.isEmpty()) {
                    if (CreateMissingDirectoriesDialog.show(window, directories)) {
                        createMissingDirectories(directories, source.getRoot(), target.getRoot(), state);
                    }
                } else {
                    JOptionPane.showMessageDialog(
                        window,
                        "No missing directories to create.",
                        getName(),
                        JOptionPane.INFORMATION_MESSAGE
                    );
                }
            }
        }
    }

    /**
     * May return null if action is canceled by user.
     */
    private @Optional List<Directory> collectMissingDirectories(@Mandatory List<Node> nodes) {
        return ProgressDialog.compute(
            window,
            "Collect missing directories",
            progress -> backupService.collectMissingDirectories(nodes, progress)
        ).getResult();
    }

    private void createMissingDirectories(
        @Mandatory List<Directory> directories,
        @Mandatory Directory source,
        @Mandatory Directory target,
        @Mandatory State state
    ) {
        Task<?> task = ProgressDialog.run(
            window,
            getName(),
            progress -> {
                backupService.createMissingDirectories(directories, source, target, progress);
                refreshService.refresh(state, progress);
            }
        );

        window.setApplicationState(state, task.getStatus());
    }
}
