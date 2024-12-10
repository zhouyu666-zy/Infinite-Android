package edu.ace.infinite.utils.http;

import android.content.Context;
import android.media.MediaPlayer;

import com.google.android.exoplayer2.source.MediaSource;
import com.google.gson.Gson;

import org.json.JSONObject;
import org.salient.artplayer.exo.ExoSourceBuilder;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import edu.ace.infinite.pojo.Like;
import edu.ace.infinite.pojo.Video;
import edu.ace.infinite.utils.ConsoleUtils;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class VideoHttpUtils {
    private static String userId = "1";
//    public final static String IP = "http://192.168.97.202:8181";
//    public final static String IP = "http://192.168.64.126:8181";
    public final static String IP = "http://172.18.3.217:8181";

    public static OkHttpClient client = new OkHttpClient()
            .newBuilder().connectTimeout(5000, TimeUnit.MILLISECONDS)
            .followRedirects(true) // 默认为 true
            .followSslRedirects(true) // 默认为 true
            .retryOnConnectionFailure(true) // 默认为 true
            .readTimeout(6000, TimeUnit.MILLISECONDS).build();

    /**
     * 获取推荐视频
     */
    public static Video getRecommentVideo(){
        Request request = new Request.Builder().url(IP + "/api/video/getRecommendList")
                .build();
        try {
            String result = client.newCall(request).execute().body().string();
            Video video = new Gson().fromJson(result, Video.class);
            // 假设 Video.Data 是一个类，video.getData() 返回一个 List<Video.Data>
            List<Video.Data> dataList = video.getData();
            //移除空对象
            dataList.removeIf(Objects::isNull);
            return video;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 喜欢/不喜欢视频
     */
    public static boolean likeVideo(boolean isLike,Video.Data video){
        Like like = new Like(userId, video, isLike);
        String json = new Gson().toJson(like);
        String url = IP;
        if(isLike){
            url += "/api/video/like";
        }else {

            url += "/api/video/dislike";
        }
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), json);
        Request request = new Request.Builder().url(url)
                .post(requestBody)
                .build();
        try {
            String result = client.newCall(request).execute().body().string();
            ConsoleUtils.logErr(result);
            JSONObject jsonObject = new JSONObject(result);
            int code = jsonObject.getInt("code");
            return code == 200;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static Video getLikeList(){
        FormBody.Builder formBody = new FormBody.Builder();
        formBody.add("userId", userId);
        Request request = new Request.Builder().url(IP + "/api/video/getLikeList")
                .post(formBody.build())
                .build();
        try {
            String result = client.newCall(request).execute().body().string();
            Video video = new Gson().fromJson(result, Video.class);
            // 假设 Video.Data 是一个类，video.getData() 返回一个 List<Video.Data>
            List<Video.Data> dataList = video.getData();
            //移除空对象
            dataList.removeIf(Objects::isNull);
            return video;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }



    public static MediaSource getMediaVideoSource(Context context, String src){
        ExoSourceBuilder exoSourceBuilder = new ExoSourceBuilder(context,src);
        Map<String, String> headers = new HashMap<>();
        headers.put("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7");
        headers.put("accept-encoding", "gzip, deflate, br, zstd");
        headers.put("accept-language", "zh-CN,zh;q=0.9");
        headers.put("priority", "u=0, i");
        headers.put("sec-ch-ua", "\"Google Chrome\";v=\"131\", \"Chromium\";v=\"131\", \"Not_A Brand\";v=\"24\"");
        headers.put("sec-ch-ua-mobile", "?0");
        headers.put("sec-ch-ua-platform", "\"Windows\"");
        headers.put("sec-fetch-dest", "document");
        headers.put("sec-fetch-mode", "navigate");
        headers.put("sec-fetch-site", "none");
        headers.put("sec-fetch-user", "?1");
        headers.put("upgrade-insecure-requests", "1");
        headers.put("user-agent", randomUA("windows"));
        headers.put("cookie", "s_v_web_id=verify_m49fh1z8_h9qgl1UQ_eDx4_4Kzx_9T3Q_VdjBm76IkXvF; __ac_nonce=0674fe3dd000f94e42b01; __ac_signature=_02B4Z6wo00f01ycsmQgAAIDC-HdqRY.Gbo8nDJ2AAK6Pd2; ttwid=1%7CY-ASPIdXQIYDERhO0F6DTdl9zf7NXCcP9NzUc3PJxeQ%7C1733288926%7Cf42adccb848c722f27814af3b06aeb009c5bbf0ce7b1c6a0820d315356762bdc; UIFID_TEMP=c4a29131752d59acb78af076c3dbdd52744118e38e80b4b96439ef1e20799db0821d02176388b15e3f30a26ce1d42517ef9fdc485c835950c69508f5e6d656147a54c77d5fb48e4f6a79d639416b6be042ea229d0cc92ea694fa96aa94e13e4e7fd94ee431fc24390f0780b18dd0e708; hevc_supported=true; my_rd=2; dy_swidth=1536; dy_sheight=864; stream_recommend_feed_params=%22%7B%5C%22cookie_enabled%5C%22%3Atrue%2C%5C%22screen_width%5C%22%3A1536%2C%5C%22screen_height%5C%22%3A864%2C%5C%22browser_online%5C%22%3Atrue%2C%5C%22cpu_core_num%5C%22%3A16%2C%5C%22device_memory%5C%22%3A8%2C%5C%22downlink%5C%22%3A10%2C%5C%22effective_type%5C%22%3A%5C%224g%5C%22%2C%5C%22round_trip_time%5C%22%3A200%7D%22; csrf_session_id=b195702ee020434f8829f06b201fb538; fpk1=U2FsdGVkX19l6kDnPJVUS280RegB2iMj2yfOR258OJLBAgLPyGecAlNJXgoM99c/6mAXpB8uEh8p4Ka+lq5DHw==; fpk2=f51bb482c660d0eeadd1f058058a2b35; bd_ticket_guard_client_web_domain=2; UIFID=c4a29131752d59acb78af076c3dbdd52744118e38e80b4b96439ef1e20799db0821d02176388b15e3f30a26ce1d42517ef9fdc485c835950c69508f5e6d656147c919eea61b3446ffa459e7d9ab032ebff559114aad96a4a39ae295307670e17a402777089aedf7bf4291cf24ae9bc282d744fc55574e9d784c0f809d39b0ef155103b96cc599caf676c4cc6e3cc2b9dac2e1aef3ca62f97dec56ed9bc678704e062d58cf94b2507c89d743fc48c57069661e769ea15f6e3aa7e48afddf46c39; douyin.com; xg_device_score=7.631555165619236; device_web_cpu_core=16; device_web_memory_size=8; architecture=amd64; is_dash_user=1; home_can_add_dy_2_desktop=%221%22; strategyABtestKey=%221733288931.907%22; FORCE_LOGIN=%7B%22videoConsumedRemainSeconds%22%3A180%7D; volume_info=%7B%22isUserMute%22%3Afalse%2C%22isMute%22%3Atrue%2C%22volume%22%3A0.5%7D; xgplayer_user_id=942378509361; passport_csrf_token=8f828bcce63db47eb8bbbf4df9e2e47c; passport_csrf_token_default=8f828bcce63db47eb8bbbf4df9e2e47c; bd_ticket_guard_client_data=eyJiZC10aWNrZXQtZ3VhcmQtdmVyc2lvbiI6MiwiYmQtdGlja2V0LWd1YXJkLWl0ZXJhdGlvbi12ZXJzaW9uIjoxLCJiZC10aWNrZXQtZ3VhcmQtcmVlLXB1YmxpYy1rZXkiOiJCS2YzdFFHZ3RBaU9ReGlkcDlKeTVmc3pGSit3T01KWTV2MUk0VXZoMGlHekl3TUovcWhkU0JtUW1UUWxsOWxyWmhyMG1ZZkpjZ2kwQ3Foa2ZRcDVac0E9IiwiYmQtdGlja2V0LWd1YXJkLXdlYi12ZXJzaW9uIjoyfQ%3D%3D; biz_trace_id=1728be45; sdk_source_info=7e276470716a68645a606960273f276364697660272927676c715a6d6069756077273f276364697660272927666d776a68605a607d71606b766c6a6b5a7666776c7571273f275e58272927666a6b766a69605a696c6061273f27636469766027292762696a6764695a7364776c6467696076273f275e5827292771273f27333c3636363c3d3d37363632342778; bit_env=Mc7WSP6dFv-37P397EFqkHShuzkZbgpL0PrUjYJjCupJKSCVIAXgAVurpxSmZ3qdwdhkhIrrCacmhl9RrWIHpZrRI115bzcxopxEcNpYLDFJFf3jAqsn-9PZ1GDBbu7nsWhjyy_Jr66YvcvzNEG_Hyy7EqMRxZwqzsStm5t5MYGGsh2cybIDgdZ4XS2SepMTu5IaK58Fc9-C8bJywtw1ws6HWAImAqNxNZ7odTKJ-5T7Svz6jKadQyx30LG7VNH4K8IbhblEWDB8uWFA2LepGrAedp9aH-xJkMdn5PUFRFsv9go-b2rejB50BlbuAqEM0PEuX_2vGwD2mDJqjfNqFufKqDidiMIcY17rmGq0EXpoW5icAGFFLcAGqMd_gecaYTUnXlt_bnQTcn7qvriFoVNCFhvq524Jstg0zl8iK7VPLR-t__KtyRjhrNV7dPfd323LZCfV6dzGc--niLzq9DYOFX7kdJ2ZmX-57ZrOHqKRny7r5eOVAwHC-bgBwcHS; gulu_source_res=eyJwX2luIjoiNGEyZmE1ZTg5YTg1M2ViNDJiOTRmMzNjODI3MThlYzAyMDdmNDc5ZjdhYTgxNmE5ZjlmZmNjNmI3OGFhZWZmNiJ9; passport_auth_mix_state=mn5u733nmjyxq37dfo6utcz15kfv3f9odb7bs4yinwb7ss1u; IsDouyinActive=false; stream_player_status_params=%22%7B%5C%22is_auto_play%5C%22%3A0%2C%5C%22is_full_screen%5C%22%3A0%2C%5C%22is_full_webscreen%5C%22%3A0%2C%5C%22is_mute%5C%22%3A1%2C%5C%22is_speed%5C%22%3A1%2C%5C%22is_visible%5C%22%3A0%7D%22");
        exoSourceBuilder.setHeaders(headers);
        exoSourceBuilder.setCacheEnable(true);
        return exoSourceBuilder.build();
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
