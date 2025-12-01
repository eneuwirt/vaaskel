package com.vaaskel.domain.security;

import com.vaaskel.domain.common.AbstractEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "USER_ROLES")
public class UserRole extends AbstractEntity {
    private String role;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public UserRole() {
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
