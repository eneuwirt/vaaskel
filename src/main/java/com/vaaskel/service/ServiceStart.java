package com.vaaskel.service;

import com.vaaskel.domain.security.entity.UserRoleType;
import com.vaaskel.domain.security.entity.User;
import com.vaaskel.domain.security.entity.UserRole;
import com.vaaskel.repository.security.UserRepository;
import com.vaaskel.repository.security.UserRoleRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

@Service
public class ServiceStart {
    private static final Logger log = Logger.getLogger(ServiceStart.class.getName());
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final BCryptPasswordEncoder encoder;

    public ServiceStart(UserRepository userRepository, UserRoleRepository userRoleRepository) {
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.encoder = new BCryptPasswordEncoder();
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

        user = new User();
        //user.setEnabled(true);
        user.setUsername("admin");
        user.setPassword(encoder.encode("admin"));
        //user.setFirstName("DELETE ME or Change Password ASAP");
        //user.setLastName("DELETE ME or Change Password ASAP");
        //user.setEmail("admin@ce-engineering.com");
        //user.setThruDate(LocalDate.now().plusDays(1));
        this.userRepository.save(user);


        role = new UserRole();
        role.setUserRoleType(UserRoleType.ADMIN);
        role.setUser(user);
        this.userRoleRepository.save(role);

        role = new UserRole();
        role.setUserRoleType(UserRoleType.USER);
        role.setUser(user);
        this.userRoleRepository.save(role);

        //settings = new UserSettings();
        //settings.setUser(user);
        //this.rep.userSettings.save(settings);
    }

}
