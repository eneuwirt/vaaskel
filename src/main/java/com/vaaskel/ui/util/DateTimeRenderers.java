package com.vaaskel.ui.util;

import com.vaadin.flow.data.renderer.LocalDateTimeRenderer;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.function.ValueProvider;

import java.time.LocalDateTime;

/**
 * Vaadin-specific renderers for date and time values.
 * Uses centralized patterns from DateTimeFormats.
 */
public final class DateTimeRenderers {

    private DateTimeRenderers() {
        // Utility class, no instances allowed
    }

    /**
     * Creates a LocalDateTimeRenderer for any bean type using the default pattern.
     *
     * @param valueProvider extracts the LocalDateTime value from the bean
     * @param <T>           bean type (e.g. UserDto)
     * @return Renderer that formats LocalDateTime for Vaadin Grid
     */
    public static <T> Renderer<T> localDateTimeRenderer(
            ValueProvider<T, LocalDateTime> valueProvider) {

        return new LocalDateTimeRenderer<>(
                valueProvider,
                DateTimeFormats.DATE_TIME_PATTERN
        );
    }

    /**
     * Overload using a custom date-time pattern.
     *
     * @param valueProvider extracts LocalDateTime from bean
     * @param pattern       date/time format pattern
     * @param <T>           bean type
     * @return Renderer using provided pattern
     */
    public static <T> Renderer<T> localDateTimeRenderer(
            ValueProvider<T, LocalDateTime> valueProvider,
            String pattern) {

        return new LocalDateTimeRenderer<>(
                valueProvider,
                pattern
        );
    }
}
