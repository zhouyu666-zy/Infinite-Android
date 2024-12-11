package edu.ace.infinite.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;

import edu.ace.infinite.R;
import edu.ace.infinite.activity.MainActivity;

public class NotificationHelper {

    private static final String CHANNEL_ID = "message_channel";
    private static final int NOTIFICATION_ID = 1;

    public static void createMessageNotification(Context context, String nickname, String message, String time, Bitmap avatar) {
        // Create an explicit intent for an Activity in your app
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Create the notification's Big View style
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.message_notification_layout);
        remoteViews.setImageViewBitmap(R.id.notification_icon, avatar);
        remoteViews.setTextViewText(R.id.notification_title, nickname);
        remoteViews.setTextViewText(R.id.notification_message, message);
        remoteViews.setTextViewText(R.id.notification_time, time);

        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Message Channel";
            String description = "Channel for message notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher_round) // 小图标
                .setContentTitle(nickname) // 标题
                .setContentText(message) // 内容
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setCustomContentView(remoteViews)
                .setAutoCancel(true); // 点击后自动取消通知

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }
}