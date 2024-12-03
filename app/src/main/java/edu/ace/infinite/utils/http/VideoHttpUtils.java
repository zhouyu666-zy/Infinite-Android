package edu.ace.infinite.utils.http;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;

import edu.ace.infinite.pojo.Video;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class VideoHttpUtils {
    public final static String IP = "http://172.22.62.100:8181";
    public static OkHttpClient client = new OkHttpClient.Builder().build();

    public static Video getRecommentVideo(){
        Request request = new Request.Builder().url(IP + "/api/video/list")
                .build();
        try {
            String result = client.newCall(request).execute().body().string();
            return new Gson().fromJson(result,Video.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
