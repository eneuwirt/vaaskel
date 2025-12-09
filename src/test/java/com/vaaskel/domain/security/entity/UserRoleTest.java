package com.vaaskel.domain.security.entity;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserRoleTest {

    @Test
    void shouldStoreAndReturnValues() {
        User user = new User();
        user.setUsername("admin");

        UserRole role = new UserRole();
        role.setUserRoleType(UserRoleType.ADMIN);
        role.setUser(user);

        assertThat(role.getUserRoleType()).isEqualTo(UserRoleType.ADMIN);
        assertThat(role.getUser()).isSameAs(user);
    }
}
