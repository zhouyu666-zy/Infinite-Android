package edu.ace.infinite.utils;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import edu.ace.infinite.R;
import edu.ace.infinite.activity.ChatActivity;
import edu.ace.infinite.activity.MainActivity;
import edu.ace.infinite.pojo.MessageListItem;
import edu.ace.infinite.view.MyDialog;

public class NotificationHelper {

    private static final String CHANNEL_ID = "message_channel";
    private static final int NOTIFICATION_ID = 1;

    public static void createMessageNotification(Context context, String nickname, String message, String time, Bitmap avatar, MessageListItem item) {
        avatar = BitmapTool.getRoundedCornerBitmap(avatar, PhoneMessage.dpToPx(24));

        ChatActivity.messageListItem = item;
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(MainActivity.OPEN_CHAT_ACTIVITY,true);
        intent.putExtra("userId", item.getUserId());
        intent.putExtra("username", item.getUsername());
        intent.putExtra("avatar", item.getAvatar());            //        intent.putExtra("avatar", avatar);       //        intent.putExtra("avatar", avatar);

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        int flags = PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE;

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, flags);


        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.message_notification_layout);
        remoteViews.setImageViewBitmap(R.id.notification_icon, avatar);
        remoteViews.setTextViewText(R.id.notification_title, nickname);
        remoteViews.setTextViewText(R.id.notification_message, message);
        remoteViews.setTextViewText(R.id.notification_time, time);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "消息";
            String description = "消息通知";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }


        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher_round) // 小图标
                .setContentTitle(nickname) // 标题
                .setContentText(message) // 内容
                .setContentIntent(pendingIntent)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setCustomContentView(remoteViews)
                .setPriority(NotificationCompat.PRIORITY_HIGH) // 设置高优先级
                .setDefaults(Notification.DEFAULT_ALL) // 启用默认的声音、震动和LED灯
                .setAutoCancel(true); // 点击后自动取消通知

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }


    public static boolean isOpenNotification(Context context){
        return NotificationManagerCompat.from(context).areNotificationsEnabled();
    }
    //申请通知权限
    public static void getNotification(Activity activity){
        if (!isOpenNotification(activity)) {
            MyDialog myNotificationDialog = new MyDialog(activity,R.style.MyDialog);
            myNotificationDialog.setCanceledOnTouchOutside(false);
            myNotificationDialog.setTitle("检测到未开启通知权限");
            myNotificationDialog.setMessage("用于弹窗提示新消息");
//        myNotificationDialog.setNoHintOnclickListener("不再提醒", () -> {
//            StorageTool.put("noAlertNotificationDialog",true);
//            myNotificationDialog.dismiss();
//        });
            myNotificationDialog.setYesOnclickListener("去开启", () -> {
                myNotificationDialog.dismiss();
                Intent intent = new Intent();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
                    intent.putExtra("android.provider.extra.APP_PACKAGE", activity.getPackageName());
                } else { //5.0
                    intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
                    intent.putExtra("app_package", activity.getPackageName());
                    intent.putExtra("app_uid", activity.getApplicationInfo().uid);
                    activity.startActivity(intent);
                }
                activity.startActivity(intent);
            });
            myNotificationDialog.setNoOnclickListener("暂不开启", myNotificationDialog::dismiss);
            myNotificationDialog.show();
        }
    }
}