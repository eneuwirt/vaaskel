package com.vaaskel.service;

import com.vaaskel.api.user.UserDto;
import com.vaaskel.domain.security.entity.User;
import com.vaaskel.domain.security.entity.UserRole;
import com.vaaskel.domain.security.entity.UserRoleType;
import com.vaaskel.repository.security.UserRepository;
import com.vaaskel.repository.security.UserRoleRepository;
import com.vaaskel.service.user.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserRoleRepository userRoleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    // --------------------
    // findUsers()
    // --------------------

    @Test
    void findUsersReturnsEmptyListWhenLimitNonPositive() {
        assertThat(userService.findUsers(0, 0)).isEmpty();
        assertThat(userService.findUsers(0, -1)).isEmpty();
        verifyNoInteractions(userRepository, userRoleRepository, passwordEncoder);
    }

    @Test
    void findUsersDelegatesToRepositoryWithCorrectPageableAndSort() {
        User user = mock(User.class);
        when(user.getId()).thenReturn(1L);
        when(user.getVersion()).thenReturn(2L);
        when(user.isReadOnly()).thenReturn(false);
        when(user.isVisible()).thenReturn(true);
        when(user.getUsername()).thenReturn("john.doe");

        Page<User> page = new PageImpl<>(List.of(user));
        when(userRepository.findAll(any(Pageable.class))).thenReturn(page);

        List<UserDto> result = userService.findUsers(20, 10);

        assertThat(result).hasSize(1);
        UserDto dto = result.get(0);
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getVersion()).isEqualTo(2L);
        assertThat(dto.isReadOnly()).isFalse();
        assertThat(dto.isVisible()).isTrue();
        assertThat(dto.getUsername()).isEqualTo("john.doe");

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(userRepository).findAll(pageableCaptor.capture());
        verifyNoMoreInteractions(userRepository);

        Pageable used = pageableCaptor.getValue();
        assertThat(used.getPageNumber()).isEqualTo(2);
        assertThat(used.getPageSize()).isEqualTo(10);
        assertThat(used.getSort()).containsExactly(Sort.Order.asc("id"));

        verifyNoInteractions(userRoleRepository, passwordEncoder);
    }

    // --------------------
    // countUsers()
    // --------------------

    @Test
    void countUsersDelegatesToRepository() {
        when(userRepository.count()).thenReturn(42L);
        assertThat(userService.countUsers()).isEqualTo(42L);
        verify(userRepository).count();
        verifyNoMoreInteractions(userRepository);
        verifyNoInteractions(userRoleRepository, passwordEncoder);
    }

    // --------------------
    // findUsersByUsername()
    // --------------------

    @Test
    void findUsersByUsernameReturnsEmptyListWhenLimitNonPositive() {
        assertThat(userService.findUsersByUsername("x", 0, 0)).isEmpty();
        assertThat(userService.findUsersByUsername("x", 0, -1)).isEmpty();
        verifyNoInteractions(userRepository, userRoleRepository, passwordEncoder);
    }

    @Test
    void findUsersByUsernameDelegatesToFindUsersWhenFilterNullOrBlank() {
        when(userRepository.findAll(any(Pageable.class))).thenReturn(Page.empty());

        userService.findUsersByUsername(null, 0, 10);
        userService.findUsersByUsername("   ", 0, 10);

        verify(userRepository, times(2)).findAll(any(Pageable.class));
        verify(userRepository, never()).findByUsernameContainingIgnoreCase(anyString(), any(Pageable.class));
        verifyNoMoreInteractions(userRepository);

        verifyNoInteractions(userRoleRepository, passwordEncoder);
    }

    @Test
    void findUsersByUsernameUsesTrimmedFilterAndCorrectPageable() {
        User user = mock(User.class);
        when(user.getId()).thenReturn(5L);
        when(user.getVersion()).thenReturn(1L);
        when(user.isReadOnly()).thenReturn(true);
        when(user.isVisible()).thenReturn(false);
        when(user.getUsername()).thenReturn("Jane");

        when(userRepository.findByUsernameContainingIgnoreCase(anyString(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(user)));

        List<UserDto> result = userService.findUsersByUsername("  ja  ", 10, 5);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(5L);
        assertThat(result.get(0).getUsername()).isEqualTo("Jane");

        ArgumentCaptor<String> filterCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);

        verify(userRepository).findByUsernameContainingIgnoreCase(filterCaptor.capture(), pageableCaptor.capture());
        verifyNoMoreInteractions(userRepository);

        assertThat(filterCaptor.getValue()).isEqualTo("ja");

        Pageable used = pageableCaptor.getValue();
        assertThat(used.getPageNumber()).isEqualTo(2);
        assertThat(used.getPageSize()).isEqualTo(5);
        assertThat(used.getSort()).containsExactly(Sort.Order.asc("id"));

        verifyNoInteractions(userRoleRepository, passwordEncoder);
    }

    // --------------------
    // countUsersByUsername()
    // --------------------

    @Test
    void countUsersByUsernameDelegatesToCountUsersWhenFilterIsNullOrBlank() {
        when(userRepository.count()).thenReturn(10L);

        assertThat(userService.countUsersByUsername(null)).isEqualTo(10L);
        assertThat(userService.countUsersByUsername("   ")).isEqualTo(10L);

        verify(userRepository, times(2)).count();
        verify(userRepository, never()).countByUsernameContainingIgnoreCase(anyString());
        verifyNoMoreInteractions(userRepository);

        verifyNoInteractions(userRoleRepository, passwordEncoder);
    }

    @Test
    void countUsersByUsernameUsesTrimmedFilter() {
        when(userRepository.countByUsernameContainingIgnoreCase(anyString())).thenReturn(3L);

        assertThat(userService.countUsersByUsername("  admin  ")).isEqualTo(3L);

        ArgumentCaptor<String> filterCaptor = ArgumentCaptor.forClass(String.class);
        verify(userRepository).countByUsernameContainingIgnoreCase(filterCaptor.capture());
        verifyNoMoreInteractions(userRepository);

        assertThat(filterCaptor.getValue()).isEqualTo("admin");

        verifyNoInteractions(userRoleRepository, passwordEncoder);
    }

    // --------------------
    // findUserById()
    // --------------------

    @Test
    void findUserByIdReturnsEmptyOptionalWhenIdIsNull() {
        assertThat(userService.findUserById(null)).isEmpty();
        verifyNoInteractions(userRepository, userRoleRepository, passwordEncoder);
    }

    @Test
    void findUserByIdReturnsEmptyOptionalWhenNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThat(userService.findUserById(99L)).isEmpty();

        verify(userRepository).findById(99L);
        verifyNoMoreInteractions(userRepository);

        verifyNoInteractions(userRoleRepository, passwordEncoder);
    }

    @Test
    void findUserByIdMapsEntityToDtoWhenFoundAndLoadsRoles() {
        User user = mock(User.class);
        when(user.getId()).thenReturn(7L);
        when(user.getVersion()).thenReturn(1L);
        when(user.isReadOnly()).thenReturn(false);
        when(user.isVisible()).thenReturn(true);
        when(user.getUsername()).thenReturn("tester");

        when(userRepository.findById(7L)).thenReturn(Optional.of(user));
        when(userRoleRepository.findAllByUserId(7L)).thenReturn(List.of(
                role(UserRoleType.ADMIN),
                role(UserRoleType.USER)
        ));

        Optional<UserDto> result = userService.findUserById(7L);

        assertThat(result).isPresent();
        assertThat(result.get().getRoles()).containsExactlyInAnyOrder(UserRoleType.ADMIN, UserRoleType.USER);

        verify(userRepository).findById(7L);
        verify(userRoleRepository).findAllByUserId(7L);
        verifyNoMoreInteractions(userRepository, userRoleRepository);
        verifyNoInteractions(passwordEncoder);
    }

    // --------------------
    // saveUser()
    // --------------------

    @Test
    void saveUserCreatesNewEntityWhenIdIsNullAndAssignsDefaultRoleWhenRolesNull() {
        UserDto dto = new UserDto();
        dto.setId(null);
        dto.setUsername("new.user");
        dto.setVisible(true);
        dto.setReadOnly(false);
        dto.setRoles(null);

        User savedEntity = mock(User.class);
        when(savedEntity.getId()).thenReturn(123L);
        when(savedEntity.getVersion()).thenReturn(0L);
        when(savedEntity.isReadOnly()).thenReturn(false);
        when(savedEntity.isVisible()).thenReturn(true);
        when(savedEntity.getUsername()).thenReturn("new.user");

        when(userRepository.save(any(User.class))).thenReturn(savedEntity);
        when(userRepository.findById(123L)).thenReturn(Optional.of(savedEntity));
        when(userRoleRepository.findAllByUserId(123L)).thenReturn(List.of(role(UserRoleType.USER)));

        UserDto result = userService.saveUser(dto);

        assertThat(result.getId()).isEqualTo(123L);
        assertThat(result.getUsername()).isEqualTo("new.user");
        assertThat(result.getRoles()).containsExactly(UserRoleType.USER);

        verify(userRepository).save(any(User.class));
        verify(userRepository).findById(123L);

        verify(userRoleRepository).deleteByUserId(123L);
        verify(userRoleRepository).save(any(UserRole.class));
        verify(userRoleRepository).findAllByUserId(123L);

        verifyNoMoreInteractions(userRepository, userRoleRepository);
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void saveUserUpdatesExistingUserAndPersistsRoles_evenIfServiceCreatesNewEntityInstance() {
        UserDto dto = new UserDto();
        dto.setId(5L);
        dto.setUsername("updated.user");
        dto.setVisible(false);
        dto.setReadOnly(true);
        dto.setRoles(EnumSet.of(UserRoleType.ADMIN));

        User existing = mock(User.class);
        when(userRepository.findById(5L)).thenReturn(Optional.of(existing));

        User savedEntity = mock(User.class);
        when(savedEntity.getId()).thenReturn(5L);
        when(savedEntity.getVersion()).thenReturn(2L);
        when(savedEntity.isReadOnly()).thenReturn(true);
        when(savedEntity.isVisible()).thenReturn(false);
        when(savedEntity.getUsername()).thenReturn("updated.user");

        // IMPORTANT: stub save for any(User), not only for 'existing'
        ArgumentCaptor<User> saveCaptor = ArgumentCaptor.forClass(User.class);
        when(userRepository.save(any(User.class))).thenReturn(savedEntity);

        // setUserRoles() loads user again after save
        when(userRepository.findById(5L)).thenReturn(Optional.of(savedEntity));
        when(userRoleRepository.findAllByUserId(5L)).thenReturn(List.of(role(UserRoleType.ADMIN)));

        UserDto result = userService.saveUser(dto);

        assertThat(result.getId()).isEqualTo(5L);
        assertThat(result.getUsername()).isEqualTo("updated.user");
        assertThat(result.getRoles()).containsExactly(UserRoleType.ADMIN);

        verify(userRepository, times(2)).findById(5L);

        verify(userRepository).save(saveCaptor.capture());
        User savedArgument = saveCaptor.getValue();

        // We can't rely on 'existing' instance; verify the values written to the saved instance:
        verify(savedArgument).setVisible(false);
        verify(savedArgument).setReadOnly(true);
        verify(savedArgument).setUsername("updated.user");

        verify(userRoleRepository).deleteByUserId(5L);
        verify(userRoleRepository).save(any(UserRole.class));
        verify(userRoleRepository).findAllByUserId(5L);

        verifyNoMoreInteractions(userRepository, userRoleRepository);
        verifyNoInteractions(passwordEncoder);
    }

    // --------------------
    // resetPassword()
    // --------------------

    @Test
    void resetPasswordEncodesAndSavesPassword() {
        User user = mock(User.class);
        when(userRepository.findById(5L)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("secret")).thenReturn("HASHED");

        User savedEntity = mock(User.class);
        when(savedEntity.getId()).thenReturn(5L);
        when(savedEntity.getVersion()).thenReturn(1L);
        when(savedEntity.isReadOnly()).thenReturn(false);
        when(savedEntity.isVisible()).thenReturn(true);
        when(savedEntity.getUsername()).thenReturn("admin");

        when(userRepository.save(any(User.class))).thenReturn(savedEntity);
        when(userRoleRepository.findAllByUserId(5L)).thenReturn(List.of(role(UserRoleType.ADMIN)));

        UserDto result = userService.resetPassword(5L, "secret");

        assertThat(result.getId()).isEqualTo(5L);
        assertThat(result.getRoles()).containsExactly(UserRoleType.ADMIN);

        verify(userRepository).findById(5L);
        verify(passwordEncoder).encode("secret");
        verify(user).setPassword("HASHED");
        verify(userRepository).save(any(User.class));
        verify(userRoleRepository).findAllByUserId(5L);

        verifyNoMoreInteractions(userRepository, userRoleRepository, passwordEncoder);
    }

    @Test
    void resetPasswordThrowsWhenUserIdNull() {
        assertThatThrownBy(() -> userService.resetPassword(null, "secret"))
                .isInstanceOf(IllegalArgumentException.class);
        verifyNoInteractions(userRepository, userRoleRepository, passwordEncoder);
    }

    @Test
    void resetPasswordThrowsWhenPasswordBlank() {
        assertThatThrownBy(() -> userService.resetPassword(1L, "   "))
                .isInstanceOf(IllegalArgumentException.class);
        verifyNoInteractions(userRepository, userRoleRepository, passwordEncoder);
    }

    @Test
    void resetPasswordThrowsWhenUserNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> userService.resetPassword(99L, "secret"))
                .isInstanceOf(IllegalArgumentException.class);

        verify(userRepository).findById(99L);
        verifyNoMoreInteractions(userRepository);
        verifyNoInteractions(userRoleRepository, passwordEncoder);
    }

    // --------------------
    // getUserRoles()
    // --------------------

    @Test
    void getUserRolesReturnsEmptyWhenUserIdNull() {
        assertThat(userService.getUserRoles(null)).isEmpty();
        verifyNoInteractions(userRepository, userRoleRepository, passwordEncoder);
    }

    @Test
    void getUserRolesReturnsEnumSetFromRepository() {
        when(userRoleRepository.findAllByUserId(10L)).thenReturn(List.of(
                role(UserRoleType.ADMIN),
                role(UserRoleType.USER)
        ));

        Set<UserRoleType> roles = userService.getUserRoles(10L);

        assertThat(roles).containsExactlyInAnyOrder(UserRoleType.ADMIN, UserRoleType.USER);

        verify(userRoleRepository).findAllByUserId(10L);
        verifyNoMoreInteractions(userRoleRepository);

        verifyNoInteractions(userRepository, passwordEncoder);
    }

    // --------------------
    // setUserRoles()
    // --------------------

    @Test
    void setUserRolesThrowsWhenUserIdNull() {
        assertThatThrownBy(() -> userService.setUserRoles(null, EnumSet.of(UserRoleType.USER)))
                .isInstanceOf(IllegalArgumentException.class);

        verifyNoInteractions(userRepository, userRoleRepository, passwordEncoder);
    }

    @Test
    void setUserRolesDeletesExistingAndSavesNewRoles() {
        User user = mock(User.class);
        when(userRepository.findById(8L)).thenReturn(Optional.of(user));

        userService.setUserRoles(8L, EnumSet.of(UserRoleType.ADMIN, UserRoleType.USER));

        verify(userRoleRepository).deleteByUserId(8L);

        ArgumentCaptor<UserRole> roleCaptor = ArgumentCaptor.forClass(UserRole.class);
        verify(userRoleRepository, times(2)).save(roleCaptor.capture());

        List<UserRole> savedRoles = roleCaptor.getAllValues();
        assertThat(savedRoles)
                .extracting(UserRole::getUserRoleType)
                .containsExactlyInAnyOrder(UserRoleType.ADMIN, UserRoleType.USER);

        assertThat(savedRoles).allSatisfy(r -> assertThat(r.getUser()).isSameAs(user));

        verify(userRepository).findById(8L);

        verifyNoMoreInteractions(userRepository, userRoleRepository);
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void setUserRolesDeletesExistingAndDoesNothingWhenRolesEmpty() {
        userService.setUserRoles(8L, EnumSet.noneOf(UserRoleType.class));

        verify(userRoleRepository).deleteByUserId(8L);
        verifyNoMoreInteractions(userRoleRepository);

        verifyNoInteractions(userRepository, passwordEncoder);
    }

    @Test
    void setUserRolesThrowsWhenUserNotFound() {
        when(userRepository.findById(77L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.setUserRoles(77L, EnumSet.of(UserRoleType.ADMIN)))
                .isInstanceOf(IllegalArgumentException.class);

        verify(userRoleRepository).deleteByUserId(77L);
        verify(userRepository).findById(77L);

        verifyNoMoreInteractions(userRepository, userRoleRepository);
        verifyNoInteractions(passwordEncoder);
    }

    private static UserRole role(UserRoleType type) {
        UserRole role = new UserRole();
        role.setUserRoleType(type);
        return role;
    }
}
