package com.vaaskel.domain.security.repository;

import com.vaaskel.domain.security.entity.User;
import com.vaaskel.repository.security.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {
    private static final String USERNAME = "admin";
    private static final String PASSWORD = "secret";

    @Autowired
    private UserRepository userRepository;

    private User createUser() {
        User user = new User();
        user.setUsername(USERNAME);
        user.setPassword(PASSWORD);
        return user;
    }

    @Test
    void persistAndLoadUserById() {
        User saved = userRepository.save(createUser());

        assertThat(saved.getId()).isNotNull();

        Optional<User> reloaded = userRepository.findById(saved.getId());
        assertThat(reloaded).isPresent();
        assertThat(reloaded.get().getUsername()).isEqualTo(USERNAME);
    }

    @Test
    void findByUsernameShouldReturnUser() {
        userRepository.save(createUser());

        Optional<User> found = userRepository.findByUsername(USERNAME);

        assertThat(found).isPresent();
        assertThat(found.get().getUsername()).isEqualTo(USERNAME);
    }
}
