package com.vaaskel.service.user;

import com.vaaskel.api.user.UserDto;

import java.util.List;
import java.util.Optional;

/**
 * Service for querying user data for the UI and API layers.
 */
public interface UserService {
    /**
     * Finds a user by their unique ID.
     *
     * @param id the unique identifier of the user
     * @return an Optional containing the UserDto if found, or empty if not found
     */
    Optional<UserDto> findUserById(Long id);

    /**
     * Fetches a slice of users for paginated UI usage.
     *
     * @param offset zero-based offset of the first row
     * @param limit  maximum number of rows to return
     * @return list of user DTOs in the requested range
     */
    List<UserDto> findUsers(int offset, int limit);

    /**
     * Returns the total number of users.
     */
    long countUsers();


    /**
     * Returns the total number of users filtered by username.
     *
     * @param username username filter (exact match)
     * @return count of users matching the filter
     */
    long countUsersByUsername(String username);

    /**
     * Fetches a slice of users filtered by username for paginated UI usage.
     *
     * @param username username filter (exact match)
     * @param offset   zero-based offset of the first row
     * @param limit    maximum number of rows to return
     * @return list of user DTOs in the requested range
     */
    List<UserDto> findUsersByUsername(String username, int offset, int limit);


    UserDto saveUser(UserDto user);
}
