package com.vaaskel.service.settings;

import com.vaaskel.domain.security.entity.User;
import com.vaaskel.domain.settings.ThemePreference;
import com.vaaskel.domain.settings.UserSettings;
import com.vaaskel.repository.settings.UserSettingsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserSettingsService {

    private final UserSettingsRepository repo;

    public UserSettingsService(UserSettingsRepository repo) {
        this.repo = repo;
    }

    @Transactional
    public UserSettings getOrCreate(User user) {
        return repo.findById(user.getId()).orElseGet(() -> repo.save(new UserSettings(user)));
    }

    @Transactional
    public void updateTheme(User user, ThemePreference pref) {
        UserSettings settings = getOrCreate(user);
        settings.setThemePreference(pref);
        repo.save(settings);
    }
}
