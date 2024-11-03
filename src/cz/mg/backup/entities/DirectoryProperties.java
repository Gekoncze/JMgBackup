package cz.mg.backup.entities;

import cz.mg.annotations.classes.Entity;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Required;
import cz.mg.annotations.storage.Value;

public @Entity class DirectoryProperties {
    private long totalSize;
    private long totalCount;
    private long totalFileCount;
    private long totalDirectoryCount;

    public DirectoryProperties() {
    }

    @Required @Value
    public long getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(long totalSize) {
        this.totalSize = totalSize;
    }

    @Required @Value
    public long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
    }

    @Required @Value
    public long getTotalFileCount() {
        return totalFileCount;
    }

    public void setTotalFileCount(long totalFileCount) {
        this.totalFileCount = totalFileCount;
    }

    @Required @Value
    public long getTotalDirectoryCount() {
        return totalDirectoryCount;
    }

    public void setTotalDirectoryCount(long totalDirectoryCount) {
        this.totalDirectoryCount = totalDirectoryCount;
    }

    public void add(@Mandatory DirectoryProperties properties) {
        this.totalSize += properties.totalSize;
        this.totalCount += properties.totalCount + 1;
        this.totalFileCount += properties.totalFileCount;
        this.totalDirectoryCount += properties.totalDirectoryCount + 1;
    }

    public void add(@Mandatory FileProperties properties) {
        this.totalSize += properties.getSize();
        this.totalCount++;
        this.totalFileCount++;
    }
}
