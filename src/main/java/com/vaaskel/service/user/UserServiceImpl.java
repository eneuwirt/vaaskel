package com.vaaskel.service.user;

import com.vaaskel.api.user.UserDto;
import com.vaaskel.domain.security.entity.User;
import com.vaaskel.domain.security.entity.UserRole;
import com.vaaskel.domain.security.entity.UserRoleType;
import com.vaaskel.repository.security.UserRepository;
import com.vaaskel.repository.security.UserRoleRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository,
            UserRoleRepository userRoleRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<UserDto> findUsers(int offset, int limit) {
        if (limit <= 0) {
            return List.of();
        }

        int page = offset / limit;

        Pageable pageable = PageRequest.of(
                page,
                limit,
                Sort.by(Sort.Direction.ASC, "id")
        );

        // NOTE: For list views, we intentionally do NOT load roles (avoid N+1).
        return userRepository.findAll(pageable)
                .stream()
                .map(this::toDtoBasic)
                .toList();
    }

    @Override
    public long countUsers() {
        return userRepository.count();
    }

    @Override
    public List<UserDto> findUsersByUsername(String username, int offset, int limit) {
        if (limit <= 0) {
            return List.of();
        }

        String filter = username != null ? username.trim() : "";
        if (filter.isEmpty()) {
            return findUsers(offset, limit);
        }

        int page = offset / limit;

        Pageable pageable = PageRequest.of(
                page,
                limit,
                Sort.by(Sort.Direction.ASC, "id")
        );

        // NOTE: Also no roles here (avoid N+1).
        return userRepository
                .findByUsernameContainingIgnoreCase(filter, pageable)
                .stream()
                .map(this::toDtoBasic)
                .toList();
    }

    @Override
    public long countUsersByUsername(String username) {
        String filter = username != null ? username.trim() : "";
        if (filter.isEmpty()) {
            return countUsers();
        }

        return userRepository.countByUsernameContainingIgnoreCase(filter);
    }

    @Override
    public Optional<UserDto> findUserById(Long id) {
        if (id == null) {
            return Optional.empty();
        }

        return userRepository.findById(id)
                .map(user -> {
                    UserDto dto = toDtoBasic(user);
                    dto.setRoles(getUserRoles(user.getId()));
                    return dto;
                });
    }

    @Override
    @Transactional
    public UserDto saveUser(UserDto dto) {
        User entity;

        if (dto.getId() != null) {
            entity = userRepository.findById(dto.getId())
                    .orElse(new User());
        } else {
            entity = new User();
        }

        entity.setVisible(dto.isVisible());
        entity.setReadOnly(dto.isReadOnly());
        entity.setUsername(dto.getUsername());

        User saved = userRepository.save(entity);

        // Persist roles if provided; default to USER for new users if null.
        Set<UserRoleType> roles = dto.getRoles();
        if (roles == null && dto.getId() == null) {
            roles = EnumSet.of(UserRoleType.USER);
        }

        if (roles != null) {
            setUserRoles(saved.getId(), roles);
        }

        UserDto out = toDtoBasic(saved);
        out.setRoles(getUserRoles(saved.getId()));
        return out;
    }

    @Override
    @Transactional
    public UserDto resetPassword(Long userId, String rawPassword) {
        if (userId == null) {
            throw new IllegalArgumentException("userId must not be null");
        }
        if (rawPassword == null || rawPassword.isBlank()) {
            throw new IllegalArgumentException("rawPassword must not be blank");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        String encoded = passwordEncoder.encode(rawPassword);
        user.setPassword(encoded);

        User saved = userRepository.save(user);

        UserDto dto = toDtoBasic(saved);
        dto.setRoles(getUserRoles(saved.getId()));
        return dto;
    }

    @Override
    public Set<UserRoleType> getUserRoles(Long userId) {
        if (userId == null) {
            return EnumSet.noneOf(UserRoleType.class);
        }

        return userRoleRepository.findAllByUserId(userId)
                .stream()
                .map(UserRole::getUserRoleType)
                .collect(() -> EnumSet.noneOf(UserRoleType.class),
                        EnumSet::add,
                        EnumSet::addAll);
    }

    @Override
    @Transactional
    public void setUserRoles(Long userId, Set<UserRoleType> roles) {
        if (userId == null) {
            throw new IllegalArgumentException("userId must not be null");
        }

        userRoleRepository.deleteByUserId(userId);

        if (roles == null || roles.isEmpty()) {
            return;
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        for (UserRoleType roleType : roles) {
            UserRole role = new UserRole();
            role.setUser(user);
            role.setUserRoleType(roleType);
            userRoleRepository.save(role);
        }
    }

    private UserDto toDtoBasic(User user) {
        UserDto dto = new UserDto();

        dto.setId(user.getId());
        dto.setVersion(user.getVersion());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setChangedAt(user.getChangedAt());
        dto.setReadOnly(user.isReadOnly());
        dto.setVisible(user.isVisible());

        dto.setUsername(user.getUsername());

        return dto;
    }
}
