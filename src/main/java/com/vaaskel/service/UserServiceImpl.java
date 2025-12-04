package com.vaaskel.service;

import com.vaaskel.api.user.UserDto;
import com.vaaskel.domain.security.entity.User;
import com.vaaskel.domain.security.repository.UserRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Default implementation of UserService.
 */
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
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
                .collect(Collectors.toList());
    }

    @Override
    public long countUsers() {
        return userRepository.count();
    }

    private UserDto toDto(User user) {
        UserDto dto = new UserDto();

        // BaseDto fields (assuming UserDto extends BaseDto)
        dto.setId(user.getId());
        dto.setVersion(user.getVersion());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setChangedAt(user.getChangedAt());
        dto.setReadOnly(user.isReadOnly());
        dto.setVisible(user.isVisible());

        // User-specific fields
        dto.setUsername(user.getUsername());
        // dto.setProfilePicture(user.getProfilePicture()); // if needed later

        return dto;
    }
}