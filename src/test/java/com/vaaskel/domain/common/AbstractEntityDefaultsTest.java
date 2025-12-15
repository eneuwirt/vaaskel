package com.vaaskel.domain.common;

import com.vaaskel.domain.security.entity.User;
import com.vaaskel.domain.security.entity.UserRoleType;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
class AbstractEntityDefaultsTest {

    @Test
    void defaultFlagsShouldBeFalse() {
        User user = new User("testuser", "password");

        assertThat(user.isReadOnly()).isFalse();
        assertThat(user.isVisible()).isFalse();
    }

    @Test
    void flagsCanBeChanged() {
        User user = new User("testuser", "password");

        user.setReadOnly(true);
        user.setVisible(true);

        assertThat(user.isReadOnly()).isTrue();
        assertThat(user.isVisible()).isTrue();
    }
}
