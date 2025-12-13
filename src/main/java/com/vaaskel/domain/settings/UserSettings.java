package com.vaaskel.domain.settings;

import com.vaaskel.domain.common.AbstractEntity;
import com.vaaskel.domain.security.entity.User;
import jakarta.persistence.*;

@Entity
@Table(name = "USER_SETTINGS")
public class UserSettings extends AbstractEntity {
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "THEME_PREFERENCE", nullable = false, length = 16)
    private ThemePreference themePreference = ThemePreference.DARK;

    protected UserSettings() {}

    public UserSettings(User user) {
        this.user = user;
        this.themePreference = ThemePreference.SYSTEM;
    }

    public ThemePreference getThemePreference() { return themePreference; }
    public void setThemePreference(ThemePreference themePreference) { this.themePreference = themePreference; }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
