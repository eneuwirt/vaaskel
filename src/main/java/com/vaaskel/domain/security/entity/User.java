package com.vaaskel.domain.security.entity;

import com.vaaskel.domain.common.AbstractEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.Set;

@Entity
@Table(
        name = "users",
        indexes = {
                @Index(name = "ux_users_user_name", columnList = "user_name", unique = true)
        }
)
public class User extends AbstractEntity {
    @Column(name="user_name")
    private String username;
    @JsonIgnore
    private String password;

    @Transient
    private Set<UserRole> roles;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Set<UserRole> getRoles() {
        return roles;
    }

    public void setRoles(Set<UserRole> roles) {
        this.roles = roles;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
