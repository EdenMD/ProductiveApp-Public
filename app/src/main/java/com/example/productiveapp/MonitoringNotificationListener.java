package com.example.productiveapp;

import android.app.Notification;
import android.content.Context;
import android.os.Build;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import androidx.annotation.RequiresApi;

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
public class MonitoringNotificationListener extends NotificationListenerService {

    private static final String TAG = "NotificationListener";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Notification Listener created.");
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        super.onNotificationPosted(sbn);
        if (sbn == null || sbn.getNotification() == null) {
            return;
        }

        String packageName = sbn.getPackageName();
        String title = null;
        String text = null;

        if (sbn.getNotification().extras != null) {
            title = sbn.getNotification().extras.getString(Notification.EXTRA_TITLE);
            text = sbn.getNotification().extras.getString(Notification.EXTRA_TEXT);
        }

        Log.d(TAG, "Notification Posted: Package=" + packageName + ", Title=" + title + ", Text=" + text);

        // TODO: Send this data to your backend
        ApiClient.sendNotificationData(this, packageName, title, text);
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        super.onNotificationRemoved(sbn);
        if (sbn == null) return;
        Log.d(TAG, "Notification Removed: Package=" + sbn.getPackageName());
        // You could also log removed notifications if needed
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Notification Listener destroyed.");
    }
}