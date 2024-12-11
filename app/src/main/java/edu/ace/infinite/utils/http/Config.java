package edu.ace.infinite.utils.http;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

public class Config {
    public final static String IP = "192.168.184.202:8181";
    public final static String BaseUrl = "http://"+ IP;
    //    public final static String IP = "http://192.168.64.126:8181";
    //    public final static String IP = "http://172.18.3.217:8181";

    public static OkHttpClient client = new OkHttpClient()
            .newBuilder().connectTimeout(5000, TimeUnit.MILLISECONDS)
            .followRedirects(true) // 默认为 true
            .followSslRedirects(true) // 默认为 true
            .retryOnConnectionFailure(true) // 默认为 true
            .readTimeout(6000, TimeUnit.MILLISECONDS).build();

}
