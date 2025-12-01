package com.vaaskel.service;

import com.vaaskel.domain.UserRoleType;
import com.vaaskel.domain.security.User;
import com.vaaskel.domain.security.UserRole;
import com.vaaskel.domain.repository.UserRepository;
import com.vaaskel.domain.repository.UserRoleRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

@Service
public class ServiceStart {
    private static final Logger log = Logger.getLogger(ServiceStart.class.getName());
    private final UserRepository repUser;
    private final UserRoleRepository repUserRole;
    private final BCryptPasswordEncoder encoder;

    public ServiceStart(UserRepository rep, UserRoleRepository repUserRole) {
        this.repUser = rep;
        this.repUserRole = repUserRole;
        this.encoder = new BCryptPasswordEncoder();
    }

    @PostConstruct
    public void firstStart() {
        User user;
        UserRole role;
        //UserSettings settings;


        if (this.repUser.count() > 0) {
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
        this.repUser.save(user);


        role = new UserRole();
        role.setRole(UserRoleType.ADMIN.name());
        role.setUser(user);
        this.repUserRole.save(role);

        role = new UserRole();
        role.setRole(UserRoleType.USER.name());
        role.setUser(user);
        this.repUserRole.save(role);

        //settings = new UserSettings();
        //settings.setUser(user);
        //this.rep.userSettings.save(settings);
    }

}
