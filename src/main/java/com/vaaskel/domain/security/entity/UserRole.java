package com.vaaskel.domain.security.entity;

import com.vaaskel.domain.common.AbstractEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "USER_ROLES")
public class UserRole extends AbstractEntity {
    @Enumerated(EnumType.STRING)
    private UserRoleType userRoleType;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public UserRole() {
    }

    public UserRoleType getUserRoleType() {
        return userRoleType;
    }

    public void setUserRoleType(UserRoleType role) {
        this.userRoleType = role;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
