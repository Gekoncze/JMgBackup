package cz.mg.backup.gui.event;

import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;

import javax.swing.*;
import java.awt.datatransfer.DataFlavor;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class UserFileDragAndDrop extends TransferHandler {
    private final @Mandatory Handler handler;

    public UserFileDragAndDrop(@Mandatory Handler handler) {
        this.handler = handler;
    }

    @Override
    public boolean canImport(TransferSupport support) {
        return support.isDataFlavorSupported(DataFlavor.javaFileListFlavor);
    }

    @Override
    public boolean importData(TransferSupport support) {
        Path directory = getSingleDirectory(support);
        if (directory != null) {
            handler.run(directory);
            return true;
        } else {
            return false;
        }
    }

    @Optional
    private Path getSingleDirectory(@Mandatory TransferSupport support) {
        try {
            if (canImport(support)) {
                List<File> files = (List<File>) support.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
                if (files.size() == 1) {
                    Path path = files.get(0).toPath();
                    if (Files.isDirectory(path)) {
                        return path;
                    }
                }
            }
            return null;
        } catch (Exception e) {
            throw new RuntimeException("Drag and drop failed.", e);
        }
    }

    public interface Handler {
        void run(@Mandatory Path path);
    }
}
