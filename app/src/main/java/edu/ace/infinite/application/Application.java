package edu.ace.infinite.application;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;


import java.io.File;

import edu.ace.infinite.utils.PhoneMessage;
import edu.ace.infinite.utils.videoCache.HttpProxyCacheServer;
import edu.ace.infinite.utils.videoCache.file.FileNameGenerator;

public class Application extends android.app.Application {
    @SuppressLint("StaticFieldLeak")
    public static Context context;

    @Override
    public void onCreate() {
        super.onCreate();

        context = getApplicationContext();
        PhoneMessage.initMessage(context);
    }

    //视频缓存
    private HttpProxyCacheServer proxy;
    public static class MyFileNameGenerator implements FileNameGenerator {
        public String generate(String ID) {
            ID = Uri.encode(ID);
            return ID+".dat";  //缓存文件命名
        }
    }
    public static HttpProxyCacheServer getProxy() {
        Application app = (Application) context;
        return app.proxy == null ? (app.proxy = app.newProxy()) : app.proxy;
    }
    private HttpProxyCacheServer newProxy() {
        HttpProxyCacheServer.Builder builder = new HttpProxyCacheServer.Builder(this)
                .cacheDirectory(new File(getDiskCachePath(this) + "/video"))
                .fileNameGenerator(new MyFileNameGenerator());
        //设置缓存大小
//        builder.maxCacheSize(MAX_MUSIC_CACHE_SIZE);
        return builder.build();
    }

    //获取APP缓存路径
    public static String getDiskCachePath(Context context) {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            return context.getExternalCacheDir().getPath();
        } else {
            return context.getCacheDir().getPath();
        }
    }

}
