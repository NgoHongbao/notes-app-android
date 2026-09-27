package com.ngohongbao.personalnotes;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/** Sau khi điện thoại khởi động lại, đăng ký lại các reminder còn hiệu lực. */
public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            for (Note n : DatabaseHelper.getInstance(context).getUpcomingReminders())
                ReminderHelper.schedule(context, n);
        }
    }
}
