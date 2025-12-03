package com.vaaskel.api.user;

import java.util.Set;

import com.vaaskel.domain.security.entity.UserRoleType;

/**
 * DTO used for CRUD dialogs and views.
 * It may contain more fields than the User entity.
 */
public class UserDto {

    // Entity-related fields
    private Long id;
    private String username;
    private String name;
    private Set<UserRoleType> roles;
    private byte[] profilePicture;

    // DTO-only fields (derived or UI-specific)
    private boolean hasProfilePicture;
    private String roleSummary;

    // --- getters and setters ---

    public Long getId() {
        return id;
    }

    public UserDto setId(Long id) {
        this.id = id;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public UserDto setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getName() {
        return name;
    }

    public UserDto setName(String name) {
        this.name = name;
        return this;
    }

    public Set<UserRoleType> getRoles() {
        return roles;
    }

    public UserDto setRoles(Set<UserRoleType> roles) {
        this.roles = roles;
        return this;
    }

    public byte[] getProfilePicture() {
        return profilePicture;
    }

    public UserDto setProfilePicture(byte[] profilePicture) {
        this.profilePicture = profilePicture;
        return this;
    }

    public boolean isHasProfilePicture() {
        return hasProfilePicture;
    }

    public UserDto setHasProfilePicture(boolean hasProfilePicture) {
        this.hasProfilePicture = hasProfilePicture;
        return this;
    }

    public String getRoleSummary() {
        return roleSummary;
    }

    public UserDto setRoleSummary(String roleSummary) {
        this.roleSummary = roleSummary;
        return this;
    }
}
