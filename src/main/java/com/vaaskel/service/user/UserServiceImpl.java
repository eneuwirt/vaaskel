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

import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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

    @Override
    @Transactional
    public UserDto saveUser(UserDto dto) {
        if (dto == null)
            throw new IllegalArgumentException("dto must not be null");
        if (dto.getId() == null)
            throw new IllegalArgumentException("dto.id must not be null for update");
        if (dto.getUsername() == null || dto.getUsername().isBlank())
            throw new IllegalArgumentException("username must not be blank");

        User entity = userRepository.findById(dto.getId())
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + dto.getId()));

        entity.setUsername(dto.getUsername().trim());
        entity.setVisible(dto.isVisible());
        entity.setReadOnly(dto.isReadOnly());

        User saved = userRepository.save(entity);

        if (dto.getRoles() != null) {
            setUserRoles(saved.getId(), dto.getRoles());
        }

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

    @Override
    @Transactional
    public void setUserRoles(Long userId, Set<UserRoleType> roles) {
        if (userId == null)
            throw new IllegalArgumentException("userId must not be null");

        userRoleRepository.deleteByUserId(userId);

        if (roles == null || roles.isEmpty())
            return;

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        var entities = roles.stream().map(rt -> new UserRole(rt, user)).toList();

        userRoleRepository.saveAll(entities);
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
