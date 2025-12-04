package com.vaaskel.ui.util;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Centralized date and time formatting for the UI layer.
 * Keeps formatting consistent across all views.
 */
public final class DateTimeFormats {

    private DateTimeFormats() {
        // Utility class, no instances allowed
    }

    /** Common date-time pattern used throughout the UI */
    public static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm";

    /** Date-only pattern used in tables and compact displays */
    public static final String DATE_PATTERN = "yyyy-MM-dd";

    /** Time-only pattern used in compact displays */
    public static final String TIME_PATTERN = "HH:mm";

    public static final DateTimeFormatter DATE_TIME =
            DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);

    public static final DateTimeFormatter DATE =
            DateTimeFormatter.ofPattern(DATE_PATTERN);

    public static final DateTimeFormatter TIME =
            DateTimeFormatter.ofPattern(TIME_PATTERN);

    /**
     * Formats a LocalDateTime as date+time, or empty string if null.
     */
    public static String formatDateTime(LocalDateTime value) {
        return value != null ? DATE_TIME.format(value) : "";
    }

    /**
     * Formats a LocalDateTime as date only, or empty string if null.
     */
    public static String formatDate(LocalDateTime value) {
        return value != null ? DATE.format(value) : "";
    }

    /**
     * Formats a LocalDateTime as time only, or empty string if null.
     */
    public static String formatTime(LocalDateTime value) {
        return value != null ? TIME.format(value) : "";
    }

    /**
     * Formats a LocalDateTime as relative time, e.g. "5 minutes ago".
     * This is intentionally simple and English-only for now.
     */
    public static String formatRelative(LocalDateTime value) {
        if (value == null) {
            return "";
        }

        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(value, now);

        long seconds = duration.getSeconds();
        if (seconds < 60) {
            return "< 1 minute ago";
        }

        long minutes = seconds / 60;
        if (minutes < 60) {
            return minutes == 1 ? "1 minute ago" : minutes + " minutes ago";
        }

        long hours = minutes / 60;
        if (hours < 24) {
            return hours == 1 ? "1 hour ago" : hours + " hours ago";
        }

        long days = hours / 24;
        if (days < 30) {
            return days == 1 ? "1 day ago" : days + " days ago";
        }

        long months = days / 30;
        if (months < 12) {
            return months == 1 ? "1 month ago" : months + " months ago";
        }

        long years = months / 12;
        return years == 1 ? "1 year ago" : years + " years ago";
    }
}
