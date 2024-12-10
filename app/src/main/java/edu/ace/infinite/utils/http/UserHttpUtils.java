package edu.ace.infinite.utils.http;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.orhanobut.hawk.Hawk;

import org.json.JSONObject;

import java.util.List;
import java.util.Objects;

import edu.ace.infinite.pojo.Video;
import edu.ace.infinite.utils.ConsoleUtils;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;

public class UserHttpUtils {
    /**
     * 登录
     */
    public static boolean login(String username,String password){
        String json= "{\"uname\":\""+username+"\",\"upass\":\""+password+"\"}";
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), json);
        Request request = new Request.Builder().url(Config.IP + "/user/loginUser")
                .post(requestBody)
                .build();
        try {
            String result = Objects.requireNonNull(Config.client.newCall(request).execute().body()).string();

            JSONObject jsonObject = new JSONObject(result);
            int code = jsonObject.getInt("code");
            if(code == 200){
                String token = jsonObject.getString("token");
                Hawk.put("loginToken", token);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 注册
     */
    public static boolean registerUser(String username,String password){
        String json= "{\"uname\":\""+username+"\",\"upass\":\""+password+"\"}";
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), json);
        Request request = new Request.Builder().url(Config.IP + "/user/registerUser")
                .post(requestBody)
                .build();
        try {
            String result = Objects.requireNonNull(Config.client.newCall(request).execute().body()).string();

            JSONObject jsonObject = new JSONObject(result);
            int code = jsonObject.getInt("code");
            if(code == 200){
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}
