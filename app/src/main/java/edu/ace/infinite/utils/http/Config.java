package edu.ace.infinite.utils.http;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

public class Config {
    public final static String IP = "192.168.232.202:8181";

//    public final static String IP = "192.168.38.27:8181";
    //    public final static String IP = "172.18.3.217:8181";
    public final static String BaseUrl = "http://" + IP;


    public static OkHttpClient client = new OkHttpClient()
            .newBuilder().connectTimeout(50000, TimeUnit.MILLISECONDS)
            .readTimeout(60000, TimeUnit.MILLISECONDS).build();

}
