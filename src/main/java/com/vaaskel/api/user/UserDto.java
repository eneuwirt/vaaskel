package com.vaaskel.api.user;

import java.util.Set;

import com.vaaskel.api.common.BaseDto;
import com.vaaskel.domain.security.entity.UserRoleType;

/**
 * DTO used for CRUD dialogs and views.
 * It may contain more fields than the User entity.
 */
public class UserDto extends BaseDto {
    // Entity-related fields
    private String username;
    private Set<UserRoleType> roles;

    public String getUsername() {
        return username;
    }

    public UserDto setUsername(String username) {
        this.username = username;
        return this;
    }

    public Set<UserRoleType> getRoles() {
        return roles;
    }

    public UserDto setRoles(Set<UserRoleType> roles) {
        this.roles = roles;
        return this;
    }
}
