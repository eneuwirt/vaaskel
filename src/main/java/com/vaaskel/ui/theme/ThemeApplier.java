package com.vaaskel.ui.theme;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.theme.lumo.Lumo;
import com.vaaskel.domain.settings.ThemePreference;

public final class ThemeApplier {

    private ThemeApplier() {}

    public static void apply(UI ui, ThemePreference pref) {
        if (pref == null) pref = ThemePreference.SYSTEM;

        switch (pref) {
            case DARK -> ui.getElement().setAttribute("theme", Lumo.DARK);
            case LIGHT -> ui.getElement().setAttribute("theme", Lumo.LIGHT);
            case SYSTEM -> applySystem(ui);
        }
    }

    private static void applySystem(UI ui) {
        // Fallback: dark if matchMedia isn't available.
        ui.getPage().executeJs("""
            (function(){
              const root = document.documentElement;
              const setTheme = (isDark) => root.setAttribute('theme', isDark ? 'dark' : 'light');
              if (!window.matchMedia) { setTheme(true); return; }
              const mq = window.matchMedia('(prefers-color-scheme: dark)');
              setTheme(mq.matches);
              if (!window.__vaaskelThemeListenerInstalled) {
                window.__vaaskelThemeListenerInstalled = true;
                mq.addEventListener('change', e => setTheme(e.matches));
              }
            })();
        """);
    }
}
