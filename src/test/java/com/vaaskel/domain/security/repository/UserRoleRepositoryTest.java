package com.vaaskel.repository.security;

import com.vaaskel.domain.security.entity.User;
import com.vaaskel.domain.security.entity.UserRole;
import com.vaaskel.domain.security.entity.UserRoleType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class UserRoleRepositoryTest {

    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_NAME = "Admin User";
    private static final String ADMIN_PASSWORD = "secret";

    private static final String USER_USERNAME = "user";
    private static final String USER_NAME = "Normal User";
    private static final String USER_PASSWORD = "user-secret";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;

    private User createAndSaveUser(String username, String password) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        return userRepository.save(user);
    }

    private UserRole createAndSaveUserRole(User user, UserRoleType type) {
        UserRole role = new UserRole();
        role.setUser(user);
        role.setUserRoleType(type);
        return userRoleRepository.save(role);
    }

    @Test
    void persistAndLoadUserRoleById() {
        User user = createAndSaveUser(ADMIN_USERNAME, ADMIN_PASSWORD);
        UserRole savedRole = createAndSaveUserRole(user, UserRoleType.ADMIN);

        assertThat(savedRole.getId()).isNotNull();

        UserRole reloaded = userRoleRepository.findById(savedRole.getId()).orElseThrow();
        assertThat(reloaded.getUser().getId()).isEqualTo(user.getId());
        assertThat(reloaded.getUserRoleType()).isEqualTo(UserRoleType.ADMIN);
    }

    @Test
    void findAllByUserIdReturnsAllRolesForUser() {
        User admin = createAndSaveUser(ADMIN_USERNAME, ADMIN_PASSWORD);
        User normalUser = createAndSaveUser(USER_USERNAME, USER_PASSWORD);

        createAndSaveUserRole(admin, UserRoleType.ADMIN);
        createAndSaveUserRole(admin, UserRoleType.USER);
        createAndSaveUserRole(normalUser, UserRoleType.USER);

        List<UserRole> adminRoles = userRoleRepository.findAllByUserId(admin.getId());

        assertThat(adminRoles).hasSize(2)
                .allSatisfy(role -> assertThat(role.getUser().getId()).isEqualTo(admin.getId()));
    }

    @Test
    void findAllByUserReturnsAllRolesForGivenUser() {
        User admin = createAndSaveUser(ADMIN_USERNAME, ADMIN_PASSWORD);

        createAndSaveUserRole(admin, UserRoleType.ADMIN);
        createAndSaveUserRole(admin, UserRoleType.USER);

        List<UserRole> roles = userRoleRepository.findAllByUser(admin);

        assertThat(roles).hasSize(2).extracting(UserRole::getUserRoleType)
                .containsExactlyInAnyOrder(UserRoleType.ADMIN, UserRoleType.USER);
    }

    @Test
    void deleteByUserIdRemovesOnlyRolesOfThatUser() {
        User admin = createAndSaveUser(ADMIN_USERNAME, ADMIN_PASSWORD);
        User normalUser = createAndSaveUser(USER_USERNAME, USER_PASSWORD);

        createAndSaveUserRole(admin, UserRoleType.ADMIN);
        createAndSaveUserRole(admin, UserRoleType.USER);
        UserRole normalRole = createAndSaveUserRole(normalUser, UserRoleType.USER);

        userRoleRepository.deleteByUserId(admin.getId());

        List<UserRole> remainingAdminRoles = userRoleRepository.findAllByUserId(admin.getId());
        List<UserRole> remainingUserRoles = userRoleRepository.findAllByUserId(normalUser.getId());

        assertThat(remainingAdminRoles).isEmpty();
        assertThat(remainingUserRoles).hasSize(1).first().extracting(UserRole::getId).isEqualTo(normalRole.getId());
    }
}