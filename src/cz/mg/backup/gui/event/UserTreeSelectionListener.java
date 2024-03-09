package cz.mg.backup.gui.event;

import cz.mg.annotations.classes.Component;
import cz.mg.annotations.requirement.Mandatory;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

public @Component class UserTreeSelectionListener implements UserListener, TreeSelectionListener {
    private final @Mandatory Handler handler;

    public UserTreeSelectionListener(@Mandatory Handler handler) {
        this.handler = handler;
    }

    @Override
    public void valueChanged(TreeSelectionEvent treeSelectionEvent) {
        handleExceptions(() -> handler.run());

    }

    public interface Handler {
        void run();
    }
}
