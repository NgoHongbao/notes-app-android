package com.ngohongbao.personalnotes;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public final class DateUtils {
    private DateUtils() {}

    public static String nowDisplay() {
        return formatDisplay(System.currentTimeMillis());
    }

    public static String formatDisplay(long time) {
        if (time <= 0) return "Không có";
        return new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(new Date(time));
    }
}
