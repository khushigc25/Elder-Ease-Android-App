package com.example.elderease.medicinesreminder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.app.NotificationManager;

public class MedicineAlarmStopReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // Stop the alarm sound
        MedicineReminderReceiver.stopAlarm();

        // Dismiss the notification
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(1);  // Cancel the reminder notification
    }
}
