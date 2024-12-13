package edu.ace.infinite.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import edu.ace.infinite.application.Application;
import edu.ace.infinite.view.MySelectDialog;
import edu.ace.infinite.view.MyToast;

public class CleanCacheUtils {
    public float imageSize = 0.00F;
    public float videoSize = 0.00F;
    public float countSize = 0.00F;
    public boolean isCacheSizeLoadComplete;
    private final Context context = Application.context;

    @SuppressLint("StaticFieldLeak")
    private static CleanCacheUtils cleanCacheUtils;
    public static synchronized CleanCacheUtils getInstance(){
        if(cleanCacheUtils == null){
            cleanCacheUtils = new CleanCacheUtils();
        }
        return cleanCacheUtils;
    }

    private final Handler cacheHandler = new Handler(Looper.getMainLooper());
    public void showDialog(Activity activity, TextView textView){
        cacheHandler.post(new Runnable() {
            @SuppressLint("SetTextI18n")
            @Override
            public void run() {
                if(cleanCacheUtils.isCacheSizeLoadComplete){
                    //M为单位
                    @SuppressLint("DefaultLocale")
                    String[] items = {"清除图片缓存:"+ cleanCacheUtils.imageSize+"M",
                            "清除视频缓存:"+ cleanCacheUtils.videoSize+"M",
                            "全部清除:"+ cleanCacheUtils.countSize +"M"};
                    MySelectDialog mySelectDialog = new MySelectDialog(activity,items,-1);
                    mySelectDialog.setTitle("请选择要清除的缓存");
                    mySelectDialog.setItemOnClickListener((index2,item,dialog) -> new Thread(() -> {
                        switch (index2){
                            case 0:
                                clearImageCache();
                                GlideCacheUtil.getInstance().clearImageAllCache(context);
                                break;
                            case 1:
                                clearVideoCache();
                                break;
                            case 2:
                                clearImageCache();
                                GlideCacheUtil.getInstance().clearImageAllCache(context);
                                clearVideoCache();
                                break;
                        }
                        activity.runOnUiThread(() -> {
                            MyToast.show("清除成功", Toast.LENGTH_LONG,true);
                            dialog.dismiss();
                            textView.setText("0.00M");
                        });
                    }).start());
                    mySelectDialog.show();
                    mySelectDialog.setOnDismissListener(dialog -> {
                        cacheHandler.removeCallbacksAndMessages(null);
                    });
                    return;
                }
                cacheHandler.postDelayed(this,10);
            }
        });
    }

    /**
     * 计算缓存大小
     */
    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    public void calculateCacheSize(TextView textView){
        isCacheSizeLoadComplete = false;
        new Thread(() -> {
            countSize = 0;
            File imageFile = new File(Application.appCachePath +"/image");
            float img_size;
            if(imageFile.exists()){
                img_size = getDirectorySize(imageFile);
            }else {
                img_size = 0.00F;
                imageSize = 0.00F;
            }
            img_size += GlideCacheUtil.getInstance().getCacheSize(context);
            imageSize = Float.parseFloat(String.format("%.2f",img_size / 1024 /1024));

            File videoFile = new File(Application.appCachePath +"/video");
            if(videoFile.exists()){
                float size = getDirectorySize(videoFile);
                videoSize = Float.parseFloat(String.format("%.2f",size / 1024 /1024));
            }else {
                videoSize = 0.00F;
            }

            countSize = Float.parseFloat(String.format("%.2f", imageSize + videoSize));
            cacheHandler.post(() -> {
                if(textView != null){
                    textView.setText((cleanCacheUtils.countSize)+"M");
                }
                isCacheSizeLoadComplete = true;
            });
        }).start();
    }

    /**
     * 图片缓存
     */
    public static void clearImageCache(){
        File file = new File(Application.appCachePath +"/image");
        if(file.exists() && file.isDirectory()){
            File[] files = file.listFiles();
            if(files != null){
                for (File f : files) {
                    f.delete();
                }
                file.delete();
            }
        }
    }

    /**
     * 获取目录大小(字节)
     *
     */
    private static long countSizeDirectory;
    private static void sizeOfDirectory(File file){
        File[] file_list = file.listFiles();
        if(file_list != null && file_list.length != 0){
            for (File list_file : file_list) {
                if(list_file.isDirectory()){
                    sizeOfDirectory(list_file);
                }else {
                    countSizeDirectory += list_file.length();
                }
            }
        }
    }
    public static long getDirectorySize(File file){
        if(file.exists()){
            if(file.isDirectory()){
                countSizeDirectory = 0;
                sizeOfDirectory(file);
                return countSizeDirectory;
            }else {
                return file.length();
            }
        }else {
            return 0;
        }
    }

    /**
     * 清除视频缓存
     */
    public static void clearVideoCache(){
        File file = new File(Application.appCachePath +"/video");
        if(file.exists() && file.isDirectory()){
            File[] files = file.listFiles();
            for (File f : files) {
                f.delete();
            }
            file.delete();
        }
    }


}
