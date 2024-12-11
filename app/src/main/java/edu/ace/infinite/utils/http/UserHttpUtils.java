package edu.ace.infinite.utils.http;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.orhanobut.hawk.Hawk;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import edu.ace.infinite.pojo.User;
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
        Request request = new Request.Builder().url(Config.BaseUrl + "/user/loginUser")
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
    public static JSONObject registerUser(String username,String password){
        String json= "{\"uname\":\""+username+"\",\"upass\":\""+password+"\"}";
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), json);
        Request request = new Request.Builder().url(Config.BaseUrl + "/user/registerUser")
                .post(requestBody)
                .build();
        try {
            String result = Objects.requireNonNull(Config.client.newCall(request).execute().body()).string();
            return new JSONObject(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<User> searchUser(String keyword) {
        String token = Hawk.get("loginToken");
        Request request = new Request.Builder().url(Config.BaseUrl + "/user/searchUser?keyword="+keyword)
                .addHeader("token", token)
                .get()
                .build();
        ArrayList<User> users = new ArrayList<>();
        try {
            String result = Objects.requireNonNull(Config.client.newCall(request).execute().body()).string();

            JSONObject jsonObject = new JSONObject(result);
            int code = jsonObject.getInt("code");
            if(code == 200){
                JSONArray data = jsonObject.getJSONArray("data");
                for (int i = 0; i < data.length(); i++) {
                    JSONObject userJson = data.getJSONObject(i);
                    User user = new Gson().fromJson(userJson.toString(), User.class);
                    users.add(user);
                }
                return users;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return users;
    }

    public static List<User> getFollowList() {
        String token = Hawk.get("loginToken");
        Request request = new Request.Builder().url(Config.BaseUrl + "/user/getFollowList")
                .addHeader("token", token)
                .get()
                .build();
        ArrayList<User> users = new ArrayList<>();
        try {
            String result = Objects.requireNonNull(Config.client.newCall(request).execute().body()).string();
            JSONObject jsonObject = new JSONObject(result);
            int code = jsonObject.getInt("code");
            ConsoleUtils.logErr(jsonObject.toString());
            if(code == 200){
                JSONArray data = jsonObject.getJSONArray("data");
                for (int i = 0; i < data.length(); i++) {
                    JSONObject userJson = data.getJSONObject(i);
                    User user = new Gson().fromJson(userJson.toString(), User.class);
                    users.add(user);
                }
                return users;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return users;
    }


    public static boolean followUser(Integer id, boolean isFollow) {
        String token = Hawk.get("loginToken");
        FormBody.Builder builder = new FormBody.Builder();
        builder.add("toUserId", String.valueOf(id));
        builder.add("isFollow", String.valueOf(isFollow));
        Request request = new Request.Builder().url(Config.BaseUrl + "/user/followUser")
                .addHeader("token", token)
                .post(builder.build())
                .build();
        try {
            String result = Objects.requireNonNull(Config.client.newCall(request).execute().body()).string();

            JSONObject jsonObject = new JSONObject(result);
            int code = jsonObject.getInt("code");
            return code == 200;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
