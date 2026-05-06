package com.vaaskel.repository.security;

import com.vaaskel.domain.security.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    @EntityGraph(attributePaths = "roles")
    Optional<User> findByUsername(String username);

    Slice<User> findAllBy(Pageable pageable);

    Slice<User> findByUsernameContainingIgnoreCase(String username, Pageable pageable);

    long countByUsernameContainingIgnoreCase(String username);
}
