package cz.mg.backup.services;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.backup.entities.File;
import cz.mg.backup.entities.Properties;
import cz.mg.backup.entities.Settings;

public @Service class FilePropertiesReader {
    private static volatile @Service FilePropertiesReader instance;

    public static @Service FilePropertiesReader getInstance() {
        if (instance == null) {
            synchronized (Service.class) {
                if (instance == null) {
                    instance = new FilePropertiesReader();
                    instance.fileSizeReader = FileSizeReader.getInstance();
                    instance.fileHashReader = FileHashReader.getInstance();
                }
            }
        }
        return instance;
    }

    private @Service FileSizeReader fileSizeReader;
    private @Service FileHashReader fileHashReader;

    private FilePropertiesReader() {
    }

    public @Mandatory Properties read(@Mandatory File file, @Mandatory Settings settings) {
        Properties properties = new Properties();
        read(file, size -> properties.setSize(size), () -> fileSizeReader.read(file.getPath()));
        read(file, hash -> properties.setHash(hash), () -> fileHashReader.read(file.getPath(), settings));
        return properties;
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
