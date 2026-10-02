package com.medipredict.util;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;

public class DateTimeUtil {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd MMM yyyy");
    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("hh:mm a");
    private static final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("dd MMM yyyy, hh:mm a");
    private static final SimpleDateFormat ISO_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    public static synchronized String formatDate(Date date) {
        if (date == null) return "N/A";
        return DATE_FORMAT.format(date);
    }

    public static synchronized String formatTimestamp(Timestamp timestamp) {
        if (timestamp == null) return "N/A";
        return DATE_TIME_FORMAT.format(timestamp);
    }

    public static synchronized String formatTime(Timestamp timestamp) {
        if (timestamp == null) return "N/A";
        return TIME_FORMAT.format(timestamp);
    }

    public static synchronized String formatIsoDate(Date date) {
        if (date == null) return "";
        return ISO_DATE_FORMAT.format(date);
    }

    public static synchronized Date parseDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) return null;
        try {
            java.util.Date parsed = ISO_DATE_FORMAT.parse(dateStr.trim());
            return new Date(parsed.getTime());
        } catch (Exception e) {
            try {
                java.util.Date parsed = DATE_FORMAT.parse(dateStr.trim());
                return new Date(parsed.getTime());
            } catch (Exception ex) {
                return null;
            }
        }
    }

    public static int calculateAge(Date dob) {
        if (dob == null) return 0;
        try {
            LocalDate birthDate = dob.toLocalDate();
            LocalDate now = LocalDate.now();
            return Period.between(birthDate, now).getYears();
        } catch (Exception e) {
            return 0;
        }
    }
}
