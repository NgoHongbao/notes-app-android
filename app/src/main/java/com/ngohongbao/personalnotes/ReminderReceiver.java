package com.ngohongbao.personalnotes;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ReminderReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        long id = intent.getLongExtra("NOTE_ID", -1);
        Note n = DatabaseHelper.getInstance(context).getNoteById(id);
        if (n == null || n.isDeleted()) return;

        Intent open = new Intent(context, NoteDetailActivity.class);
        open.putExtra("NOTE_ID", id);
        PendingIntent contentIntent = PendingIntent.getActivity(context, (int) id, open,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Notification.Builder b = android.os.Build.VERSION.SDK_INT >= 26
                ? new Notification.Builder(context, ReminderHelper.CHANNEL_ID)
                : new Notification.Builder(context);
        b.setSmallIcon(R.drawable.ic_note)
                .setContentTitle("Nhắc ghi chú: " + n.getTitle())
                .setContentText(n.getContent())
                .setContentIntent(contentIntent)
                .setAutoCancel(true);

        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (nm != null) nm.notify((int) id, b.build());
    }
}
