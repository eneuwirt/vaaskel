package com.vaaskel.domain.security.repository;

import com.vaaskel.domain.security.entity.UserRole;
import com.vaaskel.repository.security.UserRoleRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class UserRoleRepositoryTest {

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Test
    void persistAndLoadUserRole() {
        UserRole role = new UserRole();

        // TODO: adjust to your real field names
        // Example if you have something like "name" or "roleName" etc.
        // role.setName("ADMIN");
        // or:
        // role.setRoleName("ADMIN");

        UserRole saved = userRoleRepository.save(role);
        assertThat(saved.getId()).as("ID should be generated").isNotNull();

        Optional<UserRole> reloadedOpt = userRoleRepository.findById(saved.getId());
        assertThat(reloadedOpt).isPresent();

        UserRole reloaded = reloadedOpt.orElseThrow();

        // TODO: adapt to real getters
        // assertThat(reloaded.getName()).isEqualTo("ADMIN");
        // or:
        // assertThat(reloaded.getRoleName()).isEqualTo("ADMIN");
    }
}