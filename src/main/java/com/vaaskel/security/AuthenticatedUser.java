package com.vaaskel.security;

import com.vaaskel.domain.security.entity.User;
import com.vaaskel.repository.security.UserRepository;
import com.vaadin.flow.spring.security.AuthenticationContext;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
public class AuthenticatedUser {

    private final UserRepository userRepository;
    private final AuthenticationContext authenticationContext;

    public AuthenticatedUser(UserRepository userRepository, AuthenticationContext authenticationContext) {
        this.userRepository = userRepository;
        this.authenticationContext = authenticationContext;
    }

    @Transactional(readOnly = true)
    public Optional<User> get() {
        return authenticationContext.getAuthenticatedUser(UserDetails.class)
                .flatMap(ud -> userRepository.findByUsername(ud.getUsername()));
    }

    public void logout() {
        authenticationContext.logout();
    }
}
