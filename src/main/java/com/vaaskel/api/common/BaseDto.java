package com.vaaskel.api.common;

import java.time.LocalDateTime;

/**
 * Base DTO type for all transport objects.
 * Mirrors the technical metadata of AbstractEntity
 * without exposing any JPA-specific concerns.
 */
public abstract class BaseDto {

    /** Technical identifier */
    private Long id;

    /** Optimistic locking version */
    private int version;

    /** Timestamp when the object was created */
    private LocalDateTime createdAt = LocalDateTime.now();

    /** Timestamp when the object was last modified */
    private LocalDateTime changedAt = LocalDateTime.now();

    /** Indicates that the object is read-only */
    private boolean readOnly;

    /** Indicates if this object should be visible in the UI */
    private boolean visible;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getChangedAt() {
        return changedAt;
    }

    public void setChangedAt(LocalDateTime changedAt) {
        this.changedAt = changedAt;
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }
}
