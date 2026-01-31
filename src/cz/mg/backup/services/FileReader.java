package cz.mg.backup.services;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.backup.entities.File;

import java.nio.file.Path;

public @Service class FileReader {
    private static volatile @Service FileReader instance;

    public static @Service FileReader getInstance() {
        if (instance == null) {
            synchronized (Service.class) {
                if (instance == null) {
                    instance = new FileReader();
                    instance.propertiesReader = FilePropertiesReader.getInstance();
                }
            }
        }
        return instance;
    }

    private @Service FilePropertiesReader propertiesReader;

    private FileReader() {
    }

    /**
     * Reads file and its properties from given path.
     */
    public @Mandatory File read(@Mandatory Path path) {
        File file = new File();
        file.setPath(path);
        file.setRelativePath(path.getFileName());
        loadProperties(file);
        return file;
    }

    private void loadProperties(@Mandatory File file) {
        try {
            file.setProperties(propertiesReader.read(file.getPath()));
        } catch (RuntimeException e) {
            file.setError(e);
        }
    }
}
