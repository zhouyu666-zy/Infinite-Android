package edu.ace.infinite.utils;
import static edu.ace.infinite.application.Application.context;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.res.Resources;
import android.os.Build;
import android.util.Log;

public class PhoneMessage {
    public static int statusBarHeight; //状态栏高度
//    public static float scale; //px和dp转换比例

    //复制
    public static void copy(String data) {
        // 获取系统剪贴板
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        // 创建一个剪贴数据集，包含一个普通文本数据条目（需要复制的数据）,其他的还有
        // newHtmlText、
        // newIntent、
        // newUri、
        // newRawUri
        ClipData clipData = ClipData.newPlainText(null, data);

        // 把数据集设置（复制）到剪贴板
        clipboard.setPrimaryClip(clipData);
    }

    /**
     * 初始化
     * @param context 上下文
     */
    public static void initMessage(Context context){
//        scale  = context.getResources().getDisplayMetrics().density;
        statusBarHeight = getStatusBarHeight(context);
    }


    public static int getWidthPixels(){
        return context.getResources().getDisplayMetrics().widthPixels;
    }
    public static int getHeightPixels(){
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    /**
     * 获取状态栏高度
     * @param context 上下文
     * @return 状态栏高度，px值
     */
    public static int getStatusBarHeight(Context context) {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        return resources.getDimensionPixelSize(resourceId);
    }

    /**
     * dp转px
     * @param dpValue dp值
     * @return px值
     */
    public static int dpToPx(float dpValue) {
        return (int) (dpValue * context.getResources().getDisplayMetrics().density + 0.5f);
    }

    public static float dpToPxFloat(float dpValue) {
        return dpValue * context.getResources().getDisplayMetrics().density;
    }


    /**
     * 获取当前app version code
     */
    public static long getAppVersionCode() {
        long appVersionCode = 0;
        try {
            PackageInfo packageInfo = context.getApplicationContext()
                    .getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                appVersionCode = packageInfo.getLongVersionCode();
            } else {
                appVersionCode = packageInfo.versionCode;
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return appVersionCode;
    }

    /**
     * 获取当前app version name
     */
    public static String getAppVersionName() {
        String appVersionName = "";
        try {
            PackageInfo packageInfo = context.getApplicationContext()
                    .getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            appVersionName = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("", e.getMessage());
        }
        return appVersionName;
    }

    /**
     * 获取应用包名
     */
    public static synchronized String getPackageName(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            return packageInfo.packageName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 获取软件签名
     */
    public static String getSign(){
        try {
            @SuppressLint("PackageManagerGetSignatures")
            PackageInfo packageInfo=context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);
            Signature[] signatures=packageInfo.signatures;
            StringBuilder builder=new StringBuilder();
            for (Signature signature:signatures){
                builder.append(signature.toCharsString());
            }
            return builder.toString();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return "";
    }
}
