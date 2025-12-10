package com.vaaskel.domain.security.entity;

import com.vaaskel.domain.common.AbstractEntity;
import jakarta.persistence.*;

@Entity
@Table(
        name = "user_roles",
        indexes = {
                @Index(name = "ix_user_roles_user_id", columnList = "user_id"),
                @Index(name = "ux_user_roles_user_role", columnList = "user_id, user_role_type", unique = true),
                @Index(name = "ix_user_roles_role_type", columnList = "user_role_type")
        }
)
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
