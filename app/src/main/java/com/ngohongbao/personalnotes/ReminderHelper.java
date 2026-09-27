package com.ngohongbao.personalnotes;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

public final class ReminderHelper {
    public static final String CHANNEL_ID = "note_reminders";
    private ReminderHelper() {}

    public static void ensureNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID, "Nhắc ghi chú", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("Thông báo nhắc việc của ứng dụng ghi chú");
            NotificationManager nm = context.getSystemService(NotificationManager.class);
            if (nm != null) nm.createNotificationChannel(channel);
        }
    }

    /** Dùng alarm không-exact để không cần quyền SCHEDULE_EXACT_ALARM. */
    public static void schedule(Context context, Note note) {
        cancel(context, note.getId());
        if (note.getReminderTime() <= System.currentTimeMillis()) return;
        Intent i = new Intent(context, ReminderReceiver.class);
        i.putExtra("NOTE_ID", note.getId());
        PendingIntent pi = PendingIntent.getBroadcast(context, (int) note.getId(), i,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (am != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, note.getReminderTime(), pi);
            else am.set(AlarmManager.RTC_WAKEUP, note.getReminderTime(), pi);
        }
    }

    public static void cancel(Context context, long noteId) {
        Intent i = new Intent(context, ReminderReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, (int) noteId, i,
                PendingIntent.FLAG_NO_CREATE | PendingIntent.FLAG_IMMUTABLE);
        if (pi != null) {
            AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            if (am != null) am.cancel(pi);
            pi.cancel();
        }
    }
}
