package com.vaaskel.domain.security.repository;

import com.vaaskel.domain.security.entity.User;
import com.vaaskel.domain.security.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Long> {
    List<UserRole> findAllByUserId(long id);

    List<UserRole> findAllByUser(User user);

    @Modifying
    void deleteByUserId(long userId);
}
