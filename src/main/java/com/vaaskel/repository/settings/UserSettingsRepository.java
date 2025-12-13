package com.vaaskel.repository.settings;

import com.vaaskel.domain.security.entity.User;
import com.vaaskel.domain.settings.UserSettings;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserSettingsRepository extends JpaRepository<UserSettings, Long> {
    Optional<UserSettings> findByUser(User user);
}
