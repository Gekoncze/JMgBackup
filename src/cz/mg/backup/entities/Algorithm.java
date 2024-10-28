package cz.mg.backup.entities;

import cz.mg.annotations.requirement.Mandatory;

public enum Algorithm {
    MD5("MD5"),
    SHA1("SHA-1"),
    SHA256("SHA-256");

    private final @Mandatory String code;

    Algorithm(@Mandatory String code) {
        this.code = code;
    }

    public @Mandatory String getCode() {
        return code;
    }
}
