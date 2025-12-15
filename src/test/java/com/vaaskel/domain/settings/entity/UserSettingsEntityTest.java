package com.vaaskel.domain.settings.entity;

import com.vaaskel.domain.security.entity.User;
import com.vaaskel.domain.settings.ThemePreference;
import com.vaaskel.domain.settings.UserSettings;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class UserSettingsEntityTest {

    @Test
    void constructor_setsDefaultThemePreferenceToSystem() {
        User user = new User("test", "test");

        UserSettings settings = new UserSettings(user);

        assertThat(settings.getThemePreference()).isEqualTo(ThemePreference.SYSTEM);
        assertThat(settings.getUser()).isSameAs(user);
    }

    @Test
    void canChangeThemePreference() {
        User user = new User("test", "test");

        UserSettings settings = new UserSettings(user);
        settings.setThemePreference(ThemePreference.DARK);

        assertThat(settings.getThemePreference()).isEqualTo(ThemePreference.DARK);
    }
}
