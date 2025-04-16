package com.example.elderease.medicinesreminder;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import androidx.core.app.NotificationCompat;

import com.example.elderease.R;
import com.example.elderease.main.MainActivity;

public class MedicineReminderReceiver extends BroadcastReceiver {
    private static MediaPlayer mediaPlayer;

    @Override
    public void onReceive(Context context, Intent intent) {
        String title = intent.getStringExtra("title");

        // Start playing alarm sound
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(context, R.raw.alarm_tone);
            mediaPlayer.setLooping(true);
            mediaPlayer.start();
        }

        // Create "Stop Alarm" action
        Intent stopIntent = new Intent(context, MedicineAlarmStopReceiver.class);
        PendingIntent stopPendingIntent = PendingIntent.getBroadcast(
                context, 0, stopIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Create a normal intent for opening the app when clicking the notification
        Intent openAppIntent = new Intent(context, MainActivity.class);
        PendingIntent openAppPendingIntent = PendingIntent.getActivity(
                context, 0, openAppIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Show notification with "Stop Alarm" button
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = "reminder_channel";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "Reminders", NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Reminder Notifications");
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Please Take Your Medicine")
                .setContentText(title)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(openAppPendingIntent) // Open app when notification is clicked
                .addAction(R.drawable.stop, "Stop Alarm", stopPendingIntent); // Stop button

        notificationManager.notify(1, builder.build());
    }

    // Method to stop alarm
    public static void stopAlarm() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
