package com.ngohongbao.personalnotes;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.widget.TextView;

import java.util.Calendar;

/** Tiện ích chọn ngày/giờ reminder dùng chung cho màn hình thêm và sửa. */
public final class NoteFormHelper {
    private NoteFormHelper() {}

    public interface OnTimePicked { void onPicked(long time); }

    public static void pickReminder(Activity activity, long initial, TextView target, OnTimePicked callback) {
        Calendar c = Calendar.getInstance();
        if (initial > System.currentTimeMillis()) c.setTimeInMillis(initial);
        new DatePickerDialog(activity, (view, y, m, d) -> {
            Calendar picked = Calendar.getInstance();
            picked.set(y, m, d, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), 0);
            new TimePickerDialog(activity, (v, h, min) -> {
                picked.set(Calendar.HOUR_OF_DAY, h);
                picked.set(Calendar.MINUTE, min);
                long time = picked.getTimeInMillis();
                target.setText("Nhắc lúc: " + DateUtils.formatDisplay(time));
                callback.onPicked(time);
            }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true).show();
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
    }
}
