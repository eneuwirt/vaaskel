package com.vaaskel.domain.common;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@MappedSuperclass
public abstract class AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Version
    private int version;

    @Column(name = "created_at", columnDefinition = "TIMESTAMP")
    @CreationTimestamp
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "changed_at", columnDefinition = "TIMESTAMP")
    @UpdateTimestamp
    private LocalDateTime changedAt = LocalDateTime.now();

    @Column(name = "read_only")
    private boolean readOnly = false;

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

    @Column(name = "visible")
    private boolean visible = false;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getVersion() {
        return version;
    }

    @Override
    public int hashCode() {
        if (getId() != null) {
            return getId().hashCode();
        }

        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof AbstractEntity that)) {
            return false; // null or not an AbstractEntity class
        }
        if (getId() != null) {
            return getId().equals(that.getId());
        }
        return super.equals(that);
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
}
