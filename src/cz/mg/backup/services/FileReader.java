package cz.mg.backup.services;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.backup.entities.File;
import cz.mg.backup.entities.Settings;

import java.nio.file.Path;

public @Service class FileReader {
    private static volatile @Service FileReader instance;

    public static @Service FileReader getInstance() {
        if (instance == null) {
            synchronized (Service.class) {
                if (instance == null) {
                    instance = new FileReader();
                    instance.fileSizeReader = FileSizeReader.getInstance();
                    instance.fileHashReader = FileHashReader.getInstance();
                }
            }
        }
        return instance;
    }

    private @Service FileSizeReader fileSizeReader;
    private @Service FileHashReader fileHashReader;

    private FileReader() {
    }

    public @Mandatory File read(@Mandatory Path path, @Mandatory Settings settings) {
        File file = new File();
        file.setPath(path);
        read(file, file::setSize, () -> fileSizeReader.read(path));
        read(file, file::setHash, () -> fileHashReader.read(path, settings));
        return file;
    }

    private <V> void read(
        @Mandatory File file,
        @Mandatory Setter<V> setter,
        @Mandatory Reader<V> reader
    ) {
        try {
            setter.set(reader.read());
        } catch (Exception e) {
            file.getErrors().addLast(e);
        }
    }

    private interface Reader<V> {
        V read() throws Exception;
    }

    private interface Setter<V> {
        void set(@Optional V value);
    }
}
