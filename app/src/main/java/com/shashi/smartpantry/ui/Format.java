package com.shashi.smartpantry.ui;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/** Small display helpers shared by the screens. */
final class Format {

    static final String DATE_PATTERN = "yyyy-MM-dd";

    private Format() { }

    /** 2.0 -> "2", 0.5 -> "0.5" */
    static String qty(double q) {
        if (q == Math.rint(q)) return String.valueOf((long) q);
        return String.valueOf(q);
    }

    /**
     * Whole days from today until the given yyyy-MM-dd date (negative = already expired).
     * Returns Integer.MAX_VALUE when there is no valid date.
     */
    static int daysUntil(String date) {
        if (date == null || date.isEmpty()) return Integer.MAX_VALUE;
        try {
            Date expiry = new SimpleDateFormat(DATE_PATTERN, Locale.US).parse(date);
            Calendar today = Calendar.getInstance();
            today.set(Calendar.HOUR_OF_DAY, 0);
            today.set(Calendar.MINUTE, 0);
            today.set(Calendar.SECOND, 0);
            today.set(Calendar.MILLISECOND, 0);
            long diff = expiry.getTime() - today.getTimeInMillis();
            return (int) Math.round(diff / 86400000.0);
        } catch (ParseException e) {
            return Integer.MAX_VALUE;
        }
    }
}
