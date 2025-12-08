package com.vaaskel.ui.i18n;

import com.vaadin.flow.i18n.I18NProvider;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class AppI18NProvider implements I18NProvider {

    public static final String BUNDLE_PREFIX = "i18n/messages";

    private final List<Locale> locales = List.of(
            Locale.ENGLISH,
            Locale.GERMAN
    );

    @Override
    public List<Locale> getProvidedLocales() {
        return locales;
    }

    @Override
    public String getTranslation(String key, Locale locale, Object... params) {
        try {
            ResourceBundle bundle = ResourceBundle.getBundle(BUNDLE_PREFIX, locale);
            String value = bundle.getString(key);
            if (params != null && params.length > 0) {
                return String.format(value, params);
            }
            return value;
        } catch (MissingResourceException e) {
            return "!" + key + "!";
        }
    }
}
