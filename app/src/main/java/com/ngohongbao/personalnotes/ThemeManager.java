package com.ngohongbao.personalnotes;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/** Quản lý giao diện sáng/tối bằng SharedPreferences. */
public final class ThemeManager {
    private static final String PREFS = "notes_settings";
    private static final String KEY_DARK = "dark_mode";

    private ThemeManager() {}

    public static void apply(Activity activity) {
        activity.setTheme(isDark(activity) ? R.style.Theme_Notes_Dark : R.style.Theme_Notes_Light);
    }

    public static boolean isDark(Context context) {
        return context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .getBoolean(KEY_DARK, false);
    }

    public static void setDark(Context context, boolean enabled) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .edit().putBoolean(KEY_DARK, enabled).apply();
    }
}
