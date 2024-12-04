package edu.ace.infinite.utils.http;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import edu.ace.infinite.pojo.Video;
import edu.ace.infinite.utils.ConsoleUtils;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class VideoHttpUtils {
    public final static String IP = "http://192.168.61.202:8181";
    public static OkHttpClient client = new OkHttpClient()
            .newBuilder().connectTimeout(5000, TimeUnit.MILLISECONDS)
            .followRedirects(true) // 默认为 true
            .followSslRedirects(true) // 默认为 true
            .retryOnConnectionFailure(true) // 默认为 true
            .readTimeout(6000, TimeUnit.MILLISECONDS).build();

    public static Video getRecommentVideo(){
        Request request = new Request.Builder().url(IP + "/api/video/list")
                .build();
        try {
            String result = client.newCall(request).execute().body().string();
            Video video = new Gson().fromJson(result, Video.class);
            // 假设 Video.Data 是一个类，video.getData() 返回一个 List<Video.Data>
            List<Video.Data> dataList = video.getData();
            //移除空元素
            dataList.removeIf(Objects::isNull);
            return video;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String randomUA(String type){
        Random random = new Random();
        int r1 = random.nextInt(9) + 1;
        int r2 = random.nextInt(9) + 1;
        int r3 = random.nextInt(9) + 1;
        int r4 = random.nextInt(9) + 1;
        if(type.equals("android")){
            return "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/"+r1+"5."+r4+".4"+r2+"32.2"+r3+"2 Mobile Safari/5"+r4+"5.06";
        }else if(type.equals("win2")){
            return "Mozilla/5.0 (Windows; U; Windows NT 5.2;. en-US) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/"+r1+"5."+r4+".2"+r2+"32.2"+r3+"2 Mobile Safari/5"+r4+"3.06";
        }else {
            return "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/"+r1+"5."+r4+".2"+r2+"32.2"+r3+"2 Mobile Safari/5"+r4+"3.06";
        }
    }
}
