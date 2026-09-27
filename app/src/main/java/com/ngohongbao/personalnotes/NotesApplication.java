package com.ngohongbao.personalnotes;

import android.app.Application;

/** Application khởi tạo notification channel một lần khi app chạy. */
public class NotesApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ReminderHelper.ensureNotificationChannel(this);
    }
}
