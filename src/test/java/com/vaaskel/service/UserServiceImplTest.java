package com.vaaskel.service;

import com.vaaskel.api.user.UserDto;
import com.vaaskel.domain.security.entity.User;
import com.vaaskel.repository.security.UserRepository;
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

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    // --------------------
    // findUsers()
    // --------------------

    @Test
    void findUsersReturnsEmptyListWhenLimitNonPositive() {
        List<UserDto> resultZero = userService.findUsers(0, 0);
        List<UserDto> resultNegative = userService.findUsers(0, -5);

        assertThat(resultZero).isEmpty();
        assertThat(resultNegative).isEmpty();

        verifyNoInteractions(userRepository);
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

        int offset = 20;
        int limit = 10;

        List<UserDto> result = userService.findUsers(offset, limit);

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

        Pageable usedPageable = pageableCaptor.getValue();
        assertThat(usedPageable.getPageNumber()).isEqualTo(offset / limit);
        assertThat(usedPageable.getPageSize()).isEqualTo(limit);
        assertThat(usedPageable.getSort())
                .containsExactly(Sort.Order.asc("id"));
    }

    @Test
    void findUsersCalculatesPageFromOffsetAndLimitCorrectly() {
        when(userRepository.findAll(any(Pageable.class)))
                .thenReturn(Page.empty());

        record Case(int offset, int limit, int expectedPage) {}

        List<Case> cases = List.of(
                new Case(0, 10, 0),
                new Case(5, 10, 0),
                new Case(9, 10, 0),
                new Case(10, 10, 1),
                new Case(15, 10, 1),
                new Case(25, 10, 2)
        );

        for (Case c : cases) {
            userService.findUsers(c.offset(), c.limit());

            ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
            verify(userRepository, atLeastOnce()).findAll(pageableCaptor.capture());

            Pageable pageable = pageableCaptor.getValue();
            assertThat(pageable.getPageNumber()).isEqualTo(c.expectedPage());
            assertThat(pageable.getPageSize()).isEqualTo(c.limit());
        }

        verify(userRepository, times(cases.size())).findAll(any(Pageable.class));
        verifyNoMoreInteractions(userRepository);
    }

    // --------------------
    // countUsers()
    // --------------------

    @Test
    void countUsersDelegatesToRepository() {
        when(userRepository.count()).thenReturn(42L);

        long count = userService.countUsers();

        assertThat(count).isEqualTo(42L);
        verify(userRepository).count();
        verifyNoMoreInteractions(userRepository);
    }

    // --------------------
    // findUsersByUsername()
    // --------------------

    @Test
    void findUsersByUsernameReturnsEmptyListWhenLimitNonPositive() {
        List<UserDto> resultZero = userService.findUsersByUsername("john", 0, 0);
        List<UserDto> resultNegative = userService.findUsersByUsername("john", 0, -5);

        assertThat(resultZero).isEmpty();
        assertThat(resultNegative).isEmpty();

        verifyNoInteractions(userRepository);
    }

    @Test
    void findUsersByUsernameDelegatesToFindAllWhenFilterIsNullOrBlank() {
        User user = mock(User.class);
        when(user.getId()).thenReturn(1L);
        when(user.getVersion()).thenReturn(1L);
        when(user.isReadOnly()).thenReturn(false);
        when(user.isVisible()).thenReturn(true);
        when(user.getUsername()).thenReturn("john.doe");

        when(userRepository.findAll(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(user)));

        int offset = 0;
        int limit = 10;

        List<UserDto> resultNull = userService.findUsersByUsername(null, offset, limit);
        List<UserDto> resultBlank = userService.findUsersByUsername("   ", offset, limit);

        assertThat(resultNull).hasSize(1);
        assertThat(resultBlank).hasSize(1);

        // verify that findAll was used and not the filtered query
        verify(userRepository, times(2)).findAll(any(Pageable.class));
        verify(userRepository, never()).findByUsernameContainingIgnoreCase(anyString(), any(Pageable.class));
        verifyNoMoreInteractions(userRepository);
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

        int offset = 10;
        int limit = 5;
        String filter = "  ja  ";

        List<UserDto> result = userService.findUsersByUsername(filter, offset, limit);

        assertThat(result).hasSize(1);
        UserDto dto = result.get(0);
        assertThat(dto.getId()).isEqualTo(5L);
        assertThat(dto.getUsername()).isEqualTo("Jane");
        assertThat(dto.isReadOnly()).isTrue();
        assertThat(dto.isVisible()).isFalse();

        ArgumentCaptor<String> usernameCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);

        verify(userRepository).findByUsernameContainingIgnoreCase(usernameCaptor.capture(), pageableCaptor.capture());
        verifyNoMoreInteractions(userRepository);

        assertThat(usernameCaptor.getValue()).isEqualTo("ja");

        Pageable pageable = pageableCaptor.getValue();
        assertThat(pageable.getPageNumber()).isEqualTo(offset / limit);
        assertThat(pageable.getPageSize()).isEqualTo(limit);
        assertThat(pageable.getSort()).containsExactly(Sort.Order.asc("id"));
    }

    // --------------------
    // countUsersByUsername()
    // --------------------

    @Test
    void countUsersByUsernameDelegatesToCountUsersWhenFilterIsNullOrBlank() {
        when(userRepository.count()).thenReturn(10L);

        long countNull = userService.countUsersByUsername(null);
        long countBlank = userService.countUsersByUsername("   ");

        assertThat(countNull).isEqualTo(10L);
        assertThat(countBlank).isEqualTo(10L);

        verify(userRepository, times(2)).count();
        verify(userRepository, never()).countByUsernameContainingIgnoreCase(anyString());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void countUsersByUsernameUsesTrimmedFilter() {
        when(userRepository.countByUsernameContainingIgnoreCase(anyString())).thenReturn(3L);

        long count = userService.countUsersByUsername("  admin  ");

        assertThat(count).isEqualTo(3L);

        ArgumentCaptor<String> usernameCaptor = ArgumentCaptor.forClass(String.class);
        verify(userRepository).countByUsernameContainingIgnoreCase(usernameCaptor.capture());
        verifyNoMoreInteractions(userRepository);

        assertThat(usernameCaptor.getValue()).isEqualTo("admin");
    }

    // --------------------
    // findUserById()
    // --------------------

    @Test
    void findUserByIdReturnsEmptyOptionalWhenIdIsNull() {
        Optional<UserDto> result = userService.findUserById(null);

        assertThat(result).isEmpty();
        verifyNoInteractions(userRepository);
    }

    @Test
    void findUserByIdMapsEntityToDtoWhenFound() {
        User user = mock(User.class);
        when(user.getId()).thenReturn(7L);
        when(user.getVersion()).thenReturn(1L);
        when(user.isReadOnly()).thenReturn(false);
        when(user.isVisible()).thenReturn(true);
        when(user.getUsername()).thenReturn("tester");

        when(userRepository.findById(7L)).thenReturn(Optional.of(user));

        Optional<UserDto> result = userService.findUserById(7L);

        assertThat(result).isPresent();
        UserDto dto = result.get();
        assertThat(dto.getId()).isEqualTo(7L);
        assertThat(dto.getUsername()).isEqualTo("tester");
        assertThat(dto.isReadOnly()).isFalse();
        assertThat(dto.isVisible()).isTrue();

        verify(userRepository).findById(7L);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void findUserByIdReturnsEmptyOptionalWhenNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<UserDto> result = userService.findUserById(99L);

        assertThat(result).isEmpty();

        verify(userRepository).findById(99L);
        verifyNoMoreInteractions(userRepository);
    }

    // --------------------
    // saveUser()
    // --------------------

    @Test
    void saveUserCreatesNewEntityWhenIdIsNull() {
        UserDto dto = new UserDto();
        dto.setId(null);
        dto.setUsername("new.user");
        dto.setVisible(true);
        dto.setReadOnly(false);

        User savedEntity = mock(User.class);
        when(savedEntity.getId()).thenReturn(123L);
        when(savedEntity.getVersion()).thenReturn(0L);
        when(savedEntity.isReadOnly()).thenReturn(false);
        when(savedEntity.isVisible()).thenReturn(true);
        when(savedEntity.getUsername()).thenReturn("new.user");

        when(userRepository.save(any(User.class))).thenReturn(savedEntity);

        UserDto result = userService.saveUser(dto);

        assertThat(result.getId()).isEqualTo(123L);
        assertThat(result.getUsername()).isEqualTo("new.user");
        assertThat(result.isVisible()).isTrue();
        assertThat(result.isReadOnly()).isFalse();

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        verifyNoMoreInteractions(userRepository);

        User entityPassedIn = userCaptor.getValue();
        assertThat(entityPassedIn.getId()).isNull(); // new entity, id should be generated by DB
        assertThat(entityPassedIn.getUsername()).isEqualTo("new.user");
        assertThat(entityPassedIn.isVisible()).isTrue();
        assertThat(entityPassedIn.isReadOnly()).isFalse();
    }

    @Test
    void saveUserUpdatesExistingEntityWhenIdIsPresent() {
        UserDto dto = new UserDto();
        dto.setId(5L);
        dto.setUsername("updated.user");
        dto.setVisible(false);
        dto.setReadOnly(true);

        User existing = mock(User.class);
        when(userRepository.findById(5L)).thenReturn(Optional.of(existing));

        User savedEntity = mock(User.class);
        when(savedEntity.getId()).thenReturn(5L);
        when(savedEntity.getVersion()).thenReturn(2L);
        when(savedEntity.isReadOnly()).thenReturn(true);
        when(savedEntity.isVisible()).thenReturn(false);
        when(savedEntity.getUsername()).thenReturn("updated.user");

        when(userRepository.save(existing)).thenReturn(savedEntity);

        UserDto result = userService.saveUser(dto);

        assertThat(result.getId()).isEqualTo(5L);
        assertThat(result.getUsername()).isEqualTo("updated.user");
        assertThat(result.isVisible()).isFalse();
        assertThat(result.isReadOnly()).isTrue();

        verify(userRepository).findById(5L);
        verify(userRepository).save(existing);
        verifyNoMoreInteractions(userRepository);

        // verify that fields were propagated to the existing entity
        verify(existing).setVisible(false);
        verify(existing).setReadOnly(true);
        verify(existing).setUsername("updated.user");
    }
}