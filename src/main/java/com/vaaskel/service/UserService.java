package com.vaaskel.service;

import com.vaaskel.api.user.UserDto;

import java.util.List;

/**
 * Service for querying user data for the UI and API layers.
 */
public interface UserService {

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
}
