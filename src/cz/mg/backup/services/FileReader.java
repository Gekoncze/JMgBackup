package cz.mg.backup.services;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
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
                    instance.propertiesReader = FilePropertiesReader.getInstance();
                }
            }
        }
        return instance;
    }

    private @Service FilePropertiesReader propertiesReader;

    private FileReader() {
    }

    public @Mandatory File read(@Mandatory Path path, @Mandatory Settings settings) {
        File file = new File();
        file.setPath(path);
        file.setProperties(propertiesReader.read(file, settings));
        return file;
    }
}
