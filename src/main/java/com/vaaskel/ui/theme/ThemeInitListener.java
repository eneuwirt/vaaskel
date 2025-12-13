package com.vaaskel.ui.theme;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.VaadinServiceInitListener;
import com.vaaskel.domain.settings.ThemePreference;
import com.vaaskel.security.AuthenticatedUser;
import com.vaaskel.service.settings.UserSettingsService;
import org.springframework.stereotype.Component;

@Component
public class ThemeInitListener implements VaadinServiceInitListener {

    private final AuthenticatedUser authenticatedUser;
    private final UserSettingsService userSettingsService;

    public ThemeInitListener(AuthenticatedUser authenticatedUser, UserSettingsService userSettingsService) {
        this.authenticatedUser = authenticatedUser;
        this.userSettingsService = userSettingsService;
    }

    @Override
    public void serviceInit(ServiceInitEvent event) {
        event.getSource().addUIInitListener(uiEvent -> {
            UI ui = uiEvent.getUI();

            ThemePreference pref = authenticatedUser.get()
                    .map(user -> userSettingsService.getOrCreate(user).getThemePreference())
                    .orElse(ThemePreference.SYSTEM);

            ThemeApplier.apply(ui, pref);
        });
    }
}