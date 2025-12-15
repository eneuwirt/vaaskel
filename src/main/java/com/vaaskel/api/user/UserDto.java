package com.vaaskel.api.user;

import java.util.Set;

import com.vaaskel.api.common.BaseDto;
import com.vaaskel.domain.security.entity.UserRoleType;

/**
 * DTO used for CRUD dialogs and views.
 * It may contain more fields than the User entity.
 */
public class UserDto extends BaseDto {
    private String username;
    private Set<UserRoleType> roles;

    // Spring Security flags (mirrors User entity)
    private boolean enabled = true;
    private boolean accountNonLocked = true;
    private boolean accountNonExpired = true;
    private boolean credentialsNonExpired = true;

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

    public boolean isEnabled() {
        return enabled;
    }

    public UserDto setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    public void setAccountNonLocked(boolean accountNonLocked) {
        this.accountNonLocked = accountNonLocked;
    }

    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    public void setAccountNonExpired(boolean accountNonExpired) {
        this.accountNonExpired = accountNonExpired;
    }

    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    public void setCredentialsNonExpired(boolean credentialsNonExpired) {
        this.credentialsNonExpired = credentialsNonExpired;
    }
}
