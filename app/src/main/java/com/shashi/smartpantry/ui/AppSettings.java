package com.shashi.smartpantry.ui;

import android.content.Context;
import android.content.SharedPreferences;

/** Thin wrapper around SharedPreferences for the Settings screen. */
final class AppSettings {

    private static final String FILE = "smart_pantry_settings";
    private static final String KEY_EXPIRY_ALERTS = "expiry_alerts";
    private static final String KEY_ALMOST_THERE = "show_almost_there";

    private AppSettings() { }

    private static SharedPreferences prefs(Context c) {
        return c.getSharedPreferences(FILE, Context.MODE_PRIVATE);
    }

    static boolean expiryAlerts(Context c) { return prefs(c).getBoolean(KEY_EXPIRY_ALERTS, true); }
    static void setExpiryAlerts(Context c, boolean v) { prefs(c).edit().putBoolean(KEY_EXPIRY_ALERTS, v).apply(); }

    static boolean showAlmostThere(Context c) { return prefs(c).getBoolean(KEY_ALMOST_THERE, false); }
    static void setShowAlmostThere(Context c, boolean v) { prefs(c).edit().putBoolean(KEY_ALMOST_THERE, v).apply(); }
}
