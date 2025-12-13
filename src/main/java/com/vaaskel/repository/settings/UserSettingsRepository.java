package com.vaaskel.repository.settings;

import com.vaaskel.domain.settings.UserSettings;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserSettingsRepository extends JpaRepository<UserSettings, Long> {
}
