package com.vaaskel.service.user;

import com.vaaskel.api.user.UserDto;
import com.vaaskel.domain.security.entity.User;
import com.vaaskel.domain.security.entity.UserRole;
import com.vaaskel.domain.security.entity.UserRoleType;
import com.vaaskel.repository.security.UserRepository;
import com.vaaskel.repository.security.UserRoleRepository;
import org.springframework.data.domain.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, UserRoleRepository userRoleRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<UserDto> findUsers(int offset, int limit) {
        if (limit <= 0)
            return List.of();
        int page = offset / limit;

        Pageable pageable = PageRequest.of(page, limit, Sort.by(Sort.Direction.ASC, "id"));

        return userRepository.findAll(pageable).stream().map(this::toDtoBasic).toList();
    }

    @Override
    public long countUsers() {
        return userRepository.count();
    }

    @Override
    public List<UserDto> findUsersByUsername(String username, int offset, int limit) {
        if (limit <= 0)
            return List.of();

        String filter = username != null ? username.trim() : "";
        if (filter.isEmpty())
            return findUsers(offset, limit);

        int page = offset / limit;
        Pageable pageable = PageRequest.of(page, limit, Sort.by(Sort.Direction.ASC, "id"));

        return userRepository.findByUsernameContainingIgnoreCase(filter, pageable).stream().map(this::toDtoBasic)
                .toList();
    }

    @Override
    public long countUsersByUsername(String username) {
        String filter = username != null ? username.trim() : "";
        return filter.isEmpty() ? countUsers() : userRepository.countByUsernameContainingIgnoreCase(filter);
    }

    @Override
    public Optional<UserDto> findUserById(Long id) {
        if (id == null)
            return Optional.empty();

        return userRepository.findById(id).map(user -> {
            UserDto dto = toDtoBasic(user);
            dto.setRoles(getUserRoles(user.getId()));
            return dto;
        });
    }

    @Transactional
    public UserDto createUser(UserDto dto, String rawPassword) {
        if (dto == null)
            throw new IllegalArgumentException("dto must not be null");
        if (dto.getId() != null)
            throw new IllegalArgumentException("dto.id must be null for create");
        if (dto.getUsername() == null || dto.getUsername().isBlank())
            throw new IllegalArgumentException("username must not be blank");
        if (rawPassword == null || rawPassword.isBlank())
            throw new IllegalArgumentException("password must not be blank");

        User entity = new User(dto.getUsername().trim(), passwordEncoder.encode(rawPassword));
        entity.setVisible(dto.isVisible());
        entity.setReadOnly(dto.isReadOnly());

        User saved = userRepository.save(entity);

        Set<UserRoleType> roles = dto.getRoles();
        if (roles == null || roles.isEmpty()) {
            roles = EnumSet.of(UserRoleType.USER);
        }
        setUserRoles(saved.getId(), roles);

        UserDto out = toDtoBasic(saved);
        out.setRoles(getUserRoles(saved.getId()));
        return out;
    }

    @Transactional
    public UserDto saveUser(UserDto dto) {
        if (dto == null || dto.getId() == null) {
            throw new IllegalArgumentException("dto.id must not be null");
        }

        User user = userRepository.findById(dto.getId())
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + dto.getId()));

        fromDtoBasic(dto, user);

        if (dto.getRoles() != null) {
            setUserRoles(user.getId(), dto.getRoles());
        }

        UserDto out = toDtoBasic(user);
        out.setRoles(dto.getRoles() != null ? EnumSet.copyOf(dto.getRoles()) : getUserRoles(user.getId()));

        return out;
    }


    @Override
    @Transactional
    public UserDto createUser(UserDto dto) {
        if (dto == null) {
            throw new IllegalArgumentException("dto must not be null");
        }

        String username = dto.getUsername() != null ? dto.getUsername().trim() : "";
        if (username.isEmpty()) {
            throw new IllegalArgumentException("username must not be blank");
        }

        // TEMP password: random, never shown
        String tempRaw = UUID.randomUUID().toString();
        User entity = new User(username, passwordEncoder.encode(tempRaw));

        fromDtoBasic(dto, entity);

        User saved = userRepository.save(entity);

        Set<UserRoleType> roles = dto.getRoles();
        if (roles == null || roles.isEmpty()) {
            roles = EnumSet.of(UserRoleType.USER);
        }
        setUserRoles(saved.getId(), roles);

        UserDto out = toDtoBasic(saved);
        out.setRoles(getUserRoles(saved.getId()));
        return out;
    }


    @Override
    @Transactional
    public UserDto resetPassword(Long userId, String rawPassword) {
        if (userId == null)
            throw new IllegalArgumentException("userId must not be null");
        if (rawPassword == null || rawPassword.isBlank())
            throw new IllegalArgumentException("rawPassword must not be blank");

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        user.setPassword(passwordEncoder.encode(rawPassword));

        User saved = userRepository.save(user);

        UserDto dto = toDtoBasic(saved);
        dto.setRoles(getUserRoles(saved.getId()));
        return dto;
    }

    @Override
    public Set<UserRoleType> getUserRoles(Long userId) {
        if (userId == null)
            return EnumSet.noneOf(UserRoleType.class);

        return userRoleRepository.findAllByUserId(userId).stream().map(UserRole::getUserRoleType)
                .collect(() -> EnumSet.noneOf(UserRoleType.class), EnumSet::add, EnumSet::addAll);
    }

    @Transactional
    public void setUserRoles(Long userId, Set<UserRoleType> roles) {
        if (userId == null) {
            throw new IllegalArgumentException("userId must not be null");
        }

        userRoleRepository.deleteByUserId(userId);
        userRoleRepository.flush(); // ensure deletion before adding new roles

        if (roles == null || roles.isEmpty()) {
            return; // ← nothing to add
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        EnumSet<UserRoleType> unique = EnumSet.copyOf(roles);

        userRoleRepository.saveAll(
                unique.stream()
                        .map(rt -> new UserRole(rt, user))
                        .toList()
        );
    }


    private UserDto toDtoBasic(User user) {
        UserDto dto = new UserDto();

        dto.setId(user.getId());
        dto.setVersion(user.getVersion());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setChangedAt(user.getChangedAt());

        dto.setUsername(user.getUsername());

        dto.setVisible(user.isVisible());
        dto.setReadOnly(user.isReadOnly());

        dto.setEnabled(user.isEnabled());
        dto.setAccountNonLocked(user.isAccountNonLocked());
        dto.setAccountNonExpired(user.isAccountNonExpired());
        dto.setCredentialsNonExpired(user.isCredentialsNonExpired());

        return dto;
    }


    private void fromDtoBasic(UserDto dto, User entity) {
        // defensive
        if (dto == null || entity == null) {
            throw new IllegalArgumentException("dto/entity must not be null");
        }

        // identity / basic
        if (dto.getUsername() != null) {
            entity.setUsername(dto.getUsername().trim());
        }

        entity.setVisible(dto.isVisible());
        entity.setReadOnly(dto.isReadOnly());

        // security flags
        entity.setEnabled(dto.isEnabled());
        entity.setAccountNonLocked(dto.isAccountNonLocked());
        entity.setAccountNonExpired(dto.isAccountNonExpired());
        entity.setCredentialsNonExpired(dto.isCredentialsNonExpired());
    }

}
