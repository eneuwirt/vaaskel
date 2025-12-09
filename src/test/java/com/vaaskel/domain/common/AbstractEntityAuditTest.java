package com.vaaskel.domain.common;

import com.vaaskel.domain.security.entity.User;
import com.vaaskel.repository.security.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class AbstractEntityAuditTest {

    private static final String USERNAME = "audit-user";
    private static final String PASSWORD = "secret";
    private static final String UPDATED_PASSWORD = "changed-secret";

    @Autowired
    private UserRepository userRepository;

    private User createUser() {
        User user = new User();
        user.setUsername(USERNAME);
        user.setPassword(PASSWORD);
        return user;
    }

    @Test
    void createdAtAndChangedAtAreSetOnPersist() {
        User saved = userRepository.saveAndFlush(createUser());

        assertThat(saved.getCreatedAt())
                .as("createdAt should be set on persist")
                .isNotNull();

        assertThat(saved.getChangedAt())
                .as("changedAt should be set on persist")
                .isNotNull();

        assertThat(saved.getChangedAt())
                .as("changedAt should be same or after createdAt")
                .isAfterOrEqualTo(saved.getCreatedAt());

        assertThat(saved.getVersion())
                .as("Version should be initialized (0 or greater)")
                .isNotNull()
                .isGreaterThanOrEqualTo(0);
    }

    @Test
    void versionAndChangedAtUpdateOnEntityChange() {
        User saved = userRepository.saveAndFlush(createUser());

        LocalDateTime oldCreatedAt = saved.getCreatedAt();
        LocalDateTime oldChangedAt = saved.getChangedAt();
        Integer initialVersion = saved.getVersion();

        saved.setPassword(UPDATED_PASSWORD);

        User updated = userRepository.saveAndFlush(saved);

        assertThat(updated.getVersion())
                .as("Version must increase on update")
                .isNotNull()
                .isGreaterThan(initialVersion);

        assertThat(updated.getChangedAt())
                .as("changedAt must be updated after modification")
                .isAfterOrEqualTo(oldChangedAt);

        assertThat(updated.getCreatedAt())
                .as("createdAt must remain unchanged")
                .isEqualTo(oldCreatedAt);
    }
}
