package com.vaaskel.ui.theme;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.theme.lumo.Lumo;
import com.vaaskel.domain.settings.ThemePreference;

public final class ThemeApplier {

    private ThemeApplier() {}

    public static void apply(UI ui, ThemePreference pref) {
        if (pref == null) {
            pref = ThemePreference.SYSTEM;
        }

        // Always clear explicit theme first to avoid "sticking"
        ui.getElement().removeAttribute("theme");
        ui.getPage().executeJs("document.documentElement.removeAttribute('theme');");

        switch (pref) {
            case DARK -> setDark(ui);
            case LIGHT -> setLight(ui);
            case SYSTEM -> applySystem(ui);
        }
    }

    private static void setDark(UI ui) {
        ui.getElement().setAttribute("theme", Lumo.DARK);
        ui.getPage().executeJs("document.documentElement.setAttribute('theme','dark');");
    }

    private static void setLight(UI ui) {
        // "Light" is the default if we don't set Lumo.DARK.
        // Still, we set it explicitly for clarity.
        ui.getElement().setAttribute("theme", Lumo.LIGHT);
        ui.getPage().executeJs("document.documentElement.setAttribute('theme','light');");
    }

    private static void applySystem(UI ui) {
        ui.getPage().executeJs("""
            (function(){
              const root = document.documentElement;
              const mq = window.matchMedia ? window.matchMedia('(prefers-color-scheme: dark)') : null;

              const apply = () => {
                if (!mq) { root.setAttribute('theme','dark'); return; } // fallback
                if (mq.matches) root.setAttribute('theme','dark');
                else root.setAttribute('theme','light');
              };

              apply();

              if (!window.__vaaskelThemeListenerInstalled && mq) {
                window.__vaaskelThemeListenerInstalled = true;
                mq.addEventListener('change', apply);
              }
            })();
        """);
    }
}
