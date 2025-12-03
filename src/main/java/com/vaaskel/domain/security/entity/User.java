package com.vaaskel.domain.security.entity;

import com.vaaskel.domain.common.AbstractEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import java.util.Set;

@Entity
@Table(name = "USERS")
public class User extends AbstractEntity {

    private String username;
    private String name;
    @JsonIgnore
    private String password;
    @Enumerated(EnumType.STRING)
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<UserRoleType> roles;
    @Lob
    @Column(length = 1000000)
    private byte[] profilePicture;

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Set<UserRoleType> getRoles() {
        return roles;
    }
    public void setRoles(Set<UserRoleType> roles) {
        this.roles = roles;
    }
    public byte[] getProfilePicture() {
        return profilePicture;
    }
    public void setProfilePicture(byte[] profilePicture) {
        this.profilePicture = profilePicture;
    }
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
