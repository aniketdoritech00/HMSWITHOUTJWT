package com.doritech.api.Common;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DateUtil {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * yyyy-MM-dd → Date (start of day)
     * Example: 2025-01-01 → 2025-01-01 00:00:00
     */
    public static Date startOfDay(String date) {
        if (date == null || date.trim().isEmpty()) {
            return null;
        }

        LocalDateTime startDateTime =
                LocalDate.parse(date, FORMATTER).atStartOfDay();

        return Date.from(
                startDateTime
                        .atZone(ZoneId.systemDefault())
                        .toInstant()
        );
    }

    /**
     * yyyy-MM-dd → Date (end of day)
     * Example: 2025-01-01 → 2025-01-01 23:59:59.999
     */
    public static Date endOfDay(String date) {
        if (date == null || date.trim().isEmpty()) {
            return null;
        }

        LocalDateTime endDateTime =
                LocalDate.parse(date, FORMATTER).atTime(LocalTime.MAX);

        return Date.from(
                endDateTime
                        .atZone(ZoneId.systemDefault())
                        .toInstant()
        );
    }
}
