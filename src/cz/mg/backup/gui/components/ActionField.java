package cz.mg.backup.gui.components;

import cz.mg.annotations.classes.Component;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.backup.gui.actions.Action;
import cz.mg.backup.gui.event.UserFocusLostListener;
import cz.mg.backup.gui.event.UserKeyPressListener;
import cz.mg.backup.gui.event.UserMouseDoubleClickListener;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.Objects;

public abstract @Component class ActionField<T> extends JTextField {
    private final @Mandatory Action action;
    private @Optional T value;

    protected ActionField(@Mandatory Action action) {
        this.action = action;
        setEditable(false);
        addMouseListener(new UserMouseDoubleClickListener(MouseEvent.BUTTON1, this::enableChanges));
        addFocusListener(new UserFocusLostListener(this::applyChanges));
        addKeyListener(new UserKeyPressListener(KeyEvent.VK_ENTER, this::applyChanges));
        addKeyListener(new UserKeyPressListener(KeyEvent.VK_ESCAPE, this::cancelChanges));
    }

    private void enableChanges() {
        setEditable(true);
        requestFocus();
    }

    private void applyChanges() {
        setEditable(false);
        setValue(textToValue(getText()));
    }

    private void cancelChanges() {
        setEditable(false);
        setText(valueToText(value));
    }

    public @Optional T getValue() {
        return value;
    }

    public void setValue(@Optional T value) {
        if (!Objects.equals(value, this.value)) {
            setText(valueToText(value));
            this.value = value;
            action.run();
        }
    }

    protected abstract @Optional T textToValue(@Mandatory String text);
    protected abstract @Mandatory String valueToText(@Optional T value);
}
