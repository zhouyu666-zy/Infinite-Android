package edu.ace.infinite.activity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.VideoView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.exoplayer2.source.MediaSource;
import com.orhanobut.hawk.Hawk;

import org.salient.artplayer.exo.ExoMediaPlayer;
import org.salient.artplayer.exo.ExoSourceBuilder;

import java.util.List;
import java.util.Map;

import edu.ace.infinite.R;
import edu.ace.infinite.application.Application;
import edu.ace.infinite.fragment.MessageFragment;
import edu.ace.infinite.pojo.MessageListItem;
import edu.ace.infinite.utils.ConsoleUtils;
import pl.droidsonroids.gif.GifImageView;

public class StartActivity extends BaseActivity {
//    private VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_start);
        super.onCreate(savedInstanceState);
        Application.appAlive = 1;
        setStatusBarTextColor(true);


        GifImageView gifImageView = findViewById(R.id.gifImage);
        gifImageView.setImageResource(R.drawable.start_page_anim);

        skip();

//        videoView = findViewById(R.id.videoView);
//
//        // 设置视频 URI，使用 R 类中的资源 ID
//        Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.start_page_video);
//        videoView.setVideoURI(videoUri);
//
//        // 添加监听器
//        videoView.setOnPreparedListener(mp -> {
//            // 视频准备完成后自动播放
//            videoView.start();
//        });
//        videoView.setOnCompletionListener(mp -> {
//            // 当视频播放完毕时，重新开始播放
//            videoView.start();
//            skip();
//        });
//
//        // 开始准备视频
//        videoView.requestFocus();


    }

    private void skip() {
        String token = Hawk.get("loginToken");
        new Thread(() -> {
            //初始化数据
            try {
                if(token != null){
                    if(Hawk.contains("login_information")){
                        InformationChangeActivity.loginInformation = Hawk.get("login_information");
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }).start();


        new Handler().postDelayed(() -> {
            Intent intent;
            if(token == null){
                intent = new Intent(StartActivity.this, LoginActivity.class);
            }else {
                intent = new Intent(StartActivity.this, MainActivity.class);
            }
            startActivity(intent);
            finish();

        },3000);
    }

    @Override
    public void finish() {
//        videoView.resume();
        super.finish();
    }
}