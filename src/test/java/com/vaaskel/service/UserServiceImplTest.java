package com.vaaskel.service;

import com.vaaskel.api.user.UserDto;
import com.vaaskel.domain.security.entity.User;
import com.vaaskel.repository.security.UserRepository;
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

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    private static final String USERNAME = "admin";
    private static final Long USER_ID = 1L;
    private static final Integer VERSION = 2;
    private static final boolean READ_ONLY = true;
    private static final boolean VISIBLE = true;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void findUsersReturnsEmptyListWhenLimitIsZeroOrNegative() {
        List<UserDto> resultZero = userService.findUsers(0, 0);
        List<UserDto> resultNegative = userService.findUsers(0, -5);

        assertThat(resultZero).isEmpty();
        assertThat(resultNegative).isEmpty();

        verifyNoInteractions(userRepository);
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
        }

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(userRepository, times(cases.size())).findAll(pageableCaptor.capture());

        List<Pageable> allPageables = pageableCaptor.getAllValues();
        assertThat(allPageables).hasSize(cases.size());

        for (int i = 0; i < cases.size(); i++) {
            Case c = cases.get(i);
            Pageable p = allPageables.get(i);

            assertThat(p.getPageNumber())
                    .as("page number for offset=%d, limit=%d", c.offset(), c.limit())
                    .isEqualTo(c.expectedPage());

            assertThat(p.getPageSize())
                    .as("page size for offset=%d, limit=%d", c.offset(), c.limit())
                    .isEqualTo(c.limit());

            Sort.Order idOrder = p.getSort().getOrderFor("id");
            assertThat(idOrder).isNotNull();
            assertThat(idOrder.getDirection()).isEqualTo(Sort.Direction.ASC);
        }
    }

    @Test
    void findUsersUsesCorrectPagingAndSortingAndMapsToDto() {
        int offset = 20;
        int limit = 10;
        int expectedPage = offset / limit; // 2

        LocalDateTime createdAt = LocalDateTime.now().minusDays(1);
        LocalDateTime changedAt = LocalDateTime.now();

        User user = new User();
        user.setId(USER_ID);
        user.setVersion(VERSION);
        user.setCreatedAt(createdAt);
        user.setChangedAt(changedAt);
        user.setReadOnly(READ_ONLY);
        user.setVisible(VISIBLE);
        user.setUsername(USERNAME);
        user.setPassword("secret");

        Page<User> page = new PageImpl<>(List.of(user));
        when(userRepository.findAll(any(Pageable.class))).thenReturn(page);

        List<UserDto> result = userService.findUsers(offset, limit);

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(userRepository).findAll(pageableCaptor.capture());

        Pageable usedPageable = pageableCaptor.getValue();
        assertThat(usedPageable.getPageNumber()).isEqualTo(expectedPage);
        assertThat(usedPageable.getPageSize()).isEqualTo(limit);

        Sort.Order idOrder = usedPageable.getSort().getOrderFor("id");
        assertThat(idOrder).isNotNull();
        assertThat(idOrder.getDirection()).isEqualTo(Sort.Direction.ASC);

        assertThat(result).hasSize(1);
        UserDto dto = result.getFirst();

        assertThat(dto.getId()).isEqualTo(USER_ID);
        assertThat(dto.getVersion()).isEqualTo(VERSION);
        assertThat(dto.getCreatedAt()).isEqualTo(createdAt);
        assertThat(dto.getChangedAt()).isEqualTo(changedAt);
        assertThat(dto.isReadOnly()).isEqualTo(READ_ONLY);
        assertThat(dto.isVisible()).isEqualTo(VISIBLE);
        assertThat(dto.getUsername()).isEqualTo(USERNAME);
    }

    @Test
    void findUsersMapsEntityFieldsToDtoExactly() {
        int offset = 0;
        int limit = 5;

        Long id = 99L;
        Integer version = 7;
        boolean readOnly = false;
        boolean visible = true;
        String username = "john.doe";
        LocalDateTime createdAt = LocalDateTime.now().minusHours(2);
        LocalDateTime changedAt = LocalDateTime.now().minusMinutes(5);

        User user = new User();
        user.setId(id);
        user.setVersion(version);
        user.setCreatedAt(createdAt);
        user.setChangedAt(changedAt);
        user.setReadOnly(readOnly);
        user.setVisible(visible);
        user.setUsername(username);
        user.setPassword("ignored-for-dto");

        Page<User> page = new PageImpl<>(List.of(user));
        when(userRepository.findAll(any(Pageable.class))).thenReturn(page);

        List<UserDto> result = userService.findUsers(offset, limit);

        assertThat(result).hasSize(1);
        UserDto dto = result.getFirst();

        assertThat(dto.getId()).isEqualTo(id);
        assertThat(dto.getVersion()).isEqualTo(version);
        assertThat(dto.getCreatedAt()).isEqualTo(createdAt);
        assertThat(dto.getChangedAt()).isEqualTo(changedAt);
        assertThat(dto.isReadOnly()).isEqualTo(readOnly);
        assertThat(dto.isVisible()).isEqualTo(visible);
        assertThat(dto.getUsername()).isEqualTo(username);
    }

    @Test
    void countUsersDelegatesToRepository() {
        when(userRepository.count()).thenReturn(42L);

        long count = userService.countUsers();

        assertThat(count).isEqualTo(42L);
        verify(userRepository).count();
        verifyNoMoreInteractions(userRepository);
    }
}