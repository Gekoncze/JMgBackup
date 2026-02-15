package cz.mg.backup.gui.views.directory;

import cz.mg.annotations.classes.Component;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.backup.entities.Directory;
import cz.mg.backup.entities.File;
import cz.mg.backup.entities.Node;
import cz.mg.backup.exceptions.NestedException;
import cz.mg.backup.gui.icons.Icons;
import cz.mg.backup.gui.views.directory.wrapper.AbstractTreeNode;
import cz.mg.panel.Panel;
import cz.mg.panel.settings.Alignment;
import cz.mg.panel.settings.Fill;

import javax.swing.*;
import javax.swing.tree.TreeCellRenderer;
import java.awt.*;

@Component class DirectoryTreeCellRenderer implements TreeCellRenderer {
    private static final Color BG_SELECTION_COLOR = UIManager.getDefaults().getColor("List.selectionBackground");
    private static final Color FG_SELECTION_COLOR = UIManager.getDefaults().getColor("List.selectionForeground");
    private static final int MARGIN = 2;
    private static final int PADDING = 2;

    @Override
    public java.awt.Component getTreeCellRendererComponent(
        @Mandatory JTree jTree,
        @Mandatory Object object,
        boolean selected,
        boolean expanded,
        boolean leaf,
        int row,
        boolean focused
    ) {
        Node node = ((AbstractTreeNode) object).getNode();

        JLabel icon = new JLabel(getIcon(node));
        JLabel label = new JLabel(getText(node));
        label.setFont(new Font(label.getFont().getName(), Font.PLAIN, label.getFont().getSize()));

        Panel panel = new Panel(MARGIN, PADDING, Alignment.LEFT);
        panel.setForeground(selected ? FG_SELECTION_COLOR : label.getForeground());
        panel.setBackground(selected ? BG_SELECTION_COLOR : label.getBackground());
        panel.setOpaque(selected);
        panel.addHorizontal(icon, 0, 0, Alignment.LEFT, Fill.BOTH);
        panel.addHorizontal(label, 0, 0, Alignment.LEFT, Fill.BOTH);
        return panel;
    }

    private @Mandatory String getText(@Mandatory Node node) {
        return node.getPath().getFileName().toString();
    }

    private @Mandatory Icon getIcon(@Mandatory Node node) {
        if (node instanceof Directory) {
            return getDirectoryIcon((Directory) node);
        } else if (node instanceof File) {
            return getFileIcon((File) node);
        } else {
            throw new UnsupportedOperationException("Unsupported node type " + node.getClass().getSimpleName() + ".");
        }
    }

    private @Mandatory Icon getDirectoryIcon(@Mandatory Directory directory) {
        boolean hasError = directory.getError() != null;
        boolean hasInnerError = directory.getError() instanceof NestedException;

        if (hasInnerError) {
            return Icons.DIRECTORY_ERROR_NESTED_ICON;
        } else if (hasError) {
            return Icons.DIRECTORY_ERROR_ICON;
        } else {
            return Icons.DIRECTORY_ICON;
        }
    }

    private @Mandatory Icon getFileIcon(@Mandatory File file) {
        return file.getError() == null ? Icons.FILE_ICON : Icons.FILE_ERROR_ICON;
    }
}
