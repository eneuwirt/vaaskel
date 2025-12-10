package com.vaaskel.repository.security;

import com.vaaskel.domain.security.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    Optional<User> findByUsername(String username);

    // For searching users by username with pagination
    Page<User> findByUsernameContainingIgnoreCase(String username, Pageable pageable);

    // For counting users matching the username filter
    long countByUsernameContainingIgnoreCase(String username);
}
