package edu.ace.infinite.utils.http;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import edu.ace.infinite.pojo.Video;
import edu.ace.infinite.utils.ConsoleUtils;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class VideoHttpUtils {
    public final static String IP = "http://192.168.61.202:8181";
    public static OkHttpClient client = new OkHttpClient()
            .newBuilder().connectTimeout(5000, TimeUnit.MILLISECONDS)
            .readTimeout(6000, TimeUnit.MILLISECONDS).build();

    public static Video getRecommentVideo(){
        Request request = new Request.Builder().url(IP + "/api/video/list")
                .build();
        try {
            String result = client.newCall(request).execute().body().string();
            ConsoleUtils.logErr(result);
            return new Gson().fromJson(result,Video.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
