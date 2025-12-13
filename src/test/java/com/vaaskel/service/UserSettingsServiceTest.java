package com.vaaskel.service;

import com.vaaskel.domain.security.entity.User;
import com.vaaskel.domain.settings.ThemePreference;
import com.vaaskel.repository.security.UserRepository;
import com.vaaskel.repository.settings.UserSettingsRepository;
import com.vaaskel.service.settings.UserSettingsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class UserSettingsServiceTest {

    @Autowired private UserRepository userRepository;
    @Autowired private UserSettingsRepository userSettingsRepository;
    @Autowired private UserSettingsService userSettingsService;

    private User newUser() {
        User user = new User();
        user.setUsername("eduard-" + UUID.randomUUID());
        user.setPassword("dummy");
        return user;
    }

    @Test
    void getOrCreate_createsSettingsWhenMissing() {
        User user = userRepository.save(newUser());

        var settings = userSettingsService.getOrCreate(user);

        assertThat(settings.getId()).isNotNull();
        assertThat(settings.getThemePreference()).isEqualTo(ThemePreference.SYSTEM);
    }

    @Test
    void getOrCreate_returnsExistingSettings() {
        User user = userRepository.save(newUser());

        var first = userSettingsService.getOrCreate(user);
        first.setThemePreference(ThemePreference.LIGHT);
        userSettingsRepository.save(first);

        var second = userSettingsService.getOrCreate(user);

        assertThat(second.getId()).isEqualTo(first.getId());
        assertThat(second.getThemePreference()).isEqualTo(ThemePreference.LIGHT);
    }

    @Test
    void updateTheme_persistsChange() {
        User user = userRepository.save(newUser());

        userSettingsService.updateTheme(user, ThemePreference.DARK);

        var loaded = userSettingsRepository.findByUser(user);
        assertThat(loaded).isPresent();
        assertThat(loaded.get().getThemePreference()).isEqualTo(ThemePreference.DARK);
    }
}