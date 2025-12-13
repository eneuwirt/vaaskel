/**
 * The entry point of the Spring Boot application.
 * <p>
 * Use the @PWA annotation make the application installable on phones, tablets
 * and some desktop browsers.
 *
 */
package com.vaaskel;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Theme(value = "vaaskel", variant = Lumo.DARK)
public class Application implements AppShellConfigurator {

    private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);

    static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}

