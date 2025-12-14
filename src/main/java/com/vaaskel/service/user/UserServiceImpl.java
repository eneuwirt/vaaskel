package com.vaaskel.service.user;

import com.vaaskel.api.user.UserDto;
import com.vaaskel.domain.security.entity.User;
import com.vaaskel.repository.security.UserRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
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
                Sort.by(Sort.Direction.ASC, "id") // default sort: by id ascending
        );

        return userRepository.findAll(pageable)
                .stream()
                .map(this::toDto)
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
            // Delegate to generic method if no filter is provided
            return findUsers(offset, limit);
        }

        int page = offset / limit;

        Pageable pageable = PageRequest.of(
                page,
                limit,
                Sort.by(Sort.Direction.ASC, "id") // keep sort consistent with findUsers
        );

        return userRepository
                .findByUsernameContainingIgnoreCase(filter, pageable)
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public long countUsersByUsername(String username) {
        String filter = username != null ? username.trim() : "";
        if (filter.isEmpty()) {
            // Delegate to generic count if no filter is provided
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
                .map(this::toDto);
    }

    @Override
    public UserDto saveUser(UserDto dto) {
        // Map DTO to entity
        User entity;

        if (dto.getId() != null) {
            entity = userRepository.findById(dto.getId())
                    .orElse(new User());
        } else {
            entity = new User();
        }

        // Base fields (only if they should be updated manually)
        // Version is handled by JPA
        entity.setVisible(dto.isVisible());
        entity.setReadOnly(dto.isReadOnly());

        // User-specific fields
        entity.setUsername(dto.getUsername());
        // entity.setName(dto.getName());  // if needed
        // entity.setProfilePicture(dto.getProfilePicture());

        User saved = userRepository.save(entity);

        return toDto(saved);
    }

    @Override
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
        return toDto(saved);
    }



    private UserDto toDto(User user) {
        UserDto dto = new UserDto();

        // BaseDto-style fields (adapt to your actual base class)
        dto.setId(user.getId());
        dto.setVersion(user.getVersion());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setChangedAt(user.getChangedAt());
        dto.setReadOnly(user.isReadOnly());
        dto.setVisible(user.isVisible());

        // User-specific fields
        dto.setUsername(user.getUsername());
        // dto.setName(user.getName());              // if the DTO has a name field
        // dto.setProfilePicture(user.getProfilePicture()); // if needed later

        return dto;
    }
}