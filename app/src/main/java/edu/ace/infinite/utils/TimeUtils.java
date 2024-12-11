package edu.ace.infinite.utils;

import java.util.concurrent.TimeUnit;

public class TimeUtils {
    /**
     * 将时间戳转换为友好显示的时间字符串。
     */
    public static String friendlyTime(Long timestamp) {
        if (timestamp == null) {
            return "未知时间";
        }
        long now = System.currentTimeMillis();
        long diff = now - timestamp;
        if (diff < 0) {
            return "未知时间";
        }

        long diffSeconds = diff / 1000;
        long diffMinutes = diff / (60 * 1000);
        long diffHours = diff / (60 * 60 * 1000);
        long diffDays = TimeUnit.MILLISECONDS.toDays(diff);

        if (diffDays > 0) {
            return diffDays + "天前";
        } else if (diffHours > 0) {
            long remainingMinutes = diffMinutes % 60;
            return diffHours + "小时" + remainingMinutes + "分钟前";
        } else if (diffMinutes > 0) {
            return diffMinutes + "分钟前";
        } else if (diffSeconds > 0) {
            return diffSeconds + "秒前";
        } else {
            return "刚刚";
        }
    }
}
