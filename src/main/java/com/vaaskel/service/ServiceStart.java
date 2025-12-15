package com.vaaskel.service;

import com.vaaskel.domain.security.entity.UserRoleType;
import com.vaaskel.domain.security.entity.User;
import com.vaaskel.domain.security.entity.UserRole;
import com.vaaskel.repository.security.UserRepository;
import com.vaaskel.repository.security.UserRoleRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

@Service
public class ServiceStart {
    private static final Logger log = Logger.getLogger(ServiceStart.class.getName());
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder encoder;

    public ServiceStart(UserRepository userRepository, UserRoleRepository userRoleRepository, PasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.encoder = encoder;
    }

    @PostConstruct
    public void firstStart() {
        User user;
        UserRole role;
        //UserSettings settings;


        if (this.userRepository.count() > 0) {
            log.warning(" >Init user already created");

            return;
        }

        log.warning(" >First run. Init user created. REMOVE IT ASAP !!");

        user = new User("admin", encoder.encode("admin"));
        this.userRepository.save(user);


        role = new UserRole(UserRoleType.ADMIN, user);
        this.userRoleRepository.save(role);

        role = new UserRole(UserRoleType.USER, user);
        this.userRoleRepository.save(role);

        //settings = new UserSettings();
        //settings.setUser(user);
        //this.rep.userSettings.save(settings);
    }

}
