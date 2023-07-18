package cz.mg.backup.services;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;

import java.nio.charset.StandardCharsets;

public @Service class FileHashConverter {
    private static final byte[] MAP = "0123456789abcdef".getBytes(StandardCharsets.US_ASCII);

    private static volatile @Service FileHashConverter instance;

    public static @Service FileHashConverter getInstance() {
        if (instance == null) {
            synchronized (Service.class) {
                if (instance == null) {
                    instance = new FileHashConverter();
                }
            }
        }
        return instance;
    }

    private FileHashConverter() {
    }

    public @Mandatory String convert(byte[] bytes) {
        byte[] data = new byte[bytes.length * 2];
        for (int i = 0; i < bytes.length; i++) {
            int b = bytes[i] & 0xFF;
            data[i * 2] = MAP[b >>> 4];
            data[i * 2 + 1] = MAP[b & 0x0F];
        }
        return new String(data, StandardCharsets.UTF_8);
    }
}
