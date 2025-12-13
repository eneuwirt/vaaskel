package com.vaaskel.service.settings;

import com.vaaskel.domain.security.entity.User;
import com.vaaskel.domain.settings.ThemePreference;
import com.vaaskel.domain.settings.UserSettings;
import com.vaaskel.repository.security.UserRepository;
import com.vaaskel.repository.settings.UserSettingsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserSettingsService {
    private final UserRepository userRepository;
    private final UserSettingsRepository userSettingsRepository;

    public UserSettingsService(UserRepository userRepository, UserSettingsRepository repo) {
        this.userRepository = userRepository;
        this.userSettingsRepository = repo;
    }

    @Transactional
    public UserSettings getOrCreate(User user) {
        return userSettingsRepository.findByUser(user)
                .orElseGet(() -> {
                    try {
                        UserSettings created = new UserSettings(userRepository.findById(user.getId()).orElseThrow());
                        return userSettingsRepository.saveAndFlush(created);
                    } catch (org.springframework.dao.DataIntegrityViolationException e) {
                        // Another request created it concurrently -> load existing
                        return userSettingsRepository.findByUser(user).orElseThrow();
                    }
                });
    }

    @Transactional
    public void updateTheme(User user, ThemePreference pref) {
        UserSettings settings = getOrCreate(user);
        settings.setThemePreference(pref);
        userSettingsRepository.save(settings);
    }
}
