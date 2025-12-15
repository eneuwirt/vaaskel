package com.vaaskel.domain.common;

import com.vaaskel.domain.security.entity.User;
import com.vaaskel.repository.security.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class AbstractEntityAuditTest {

    private static final String USERNAME = "audit-user";
    private static final String PASSWORD = "secret";
    private static final String UPDATED_PASSWORD = "changed-secret";

    private final UserRepository userRepository;

    @Autowired
    AbstractEntityAuditTest(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    private User createUser() {
        User user = new User(USERNAME, PASSWORD);
        return user;
    }

    @Test
    void createdAtAndChangedAtAreSetOnPersist() {
        User saved = userRepository.saveAndFlush(createUser());

        LocalDateTime createdAt = saved.getCreatedAt();
        LocalDateTime changedAt = saved.getChangedAt();

        assertThat(createdAt).as("createdAt should be set on persist").isNotNull();

        assertThat(changedAt).as("changedAt should be set on persist").isNotNull();

        // We only assert that both timestamps are close to each other,
        // not that one is strictly before/after the other on microsecond level.
        Duration diff = Duration.between(createdAt, changedAt).abs();
        assertThat(diff).as("createdAt and changedAt should not differ too much").isLessThan(Duration.ofSeconds(1));

        assertThat(saved.getVersion()).as("Version should be initialized (0 or greater)").isNotNull()
                .isGreaterThanOrEqualTo(0);
    }

    @Test
    void versionAndChangedAtUpdateOnEntityChange() {
        User saved = userRepository.saveAndFlush(createUser());

        LocalDateTime oldCreatedAt = saved.getCreatedAt();
        LocalDateTime oldChangedAt = saved.getChangedAt();
        Long initialVersion = saved.getVersion();

        saved.setPassword(UPDATED_PASSWORD);

        User updated = userRepository.saveAndFlush(saved);

        assertThat(updated.getVersion()).as("Version must increase on update").isNotNull()
                .isGreaterThan(initialVersion);

        assertThat(updated.getChangedAt()).as("changedAt must be updated after modification")
                .isAfterOrEqualTo(oldChangedAt);

        assertThat(updated.getCreatedAt()).as("createdAt must remain unchanged").isEqualTo(oldCreatedAt);
    }
}
