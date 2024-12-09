package edu.ace.infinite.activity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.VideoView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.exoplayer2.source.MediaSource;

import org.salient.artplayer.exo.ExoMediaPlayer;
import org.salient.artplayer.exo.ExoSourceBuilder;

import java.util.Map;

import edu.ace.infinite.R;
import edu.ace.infinite.application.Application;

public class StartActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_start);
        super.onCreate(savedInstanceState);

//        VideoView videoView = findViewById(R.id.videoView);
//        ExoMediaPlayer exoMediaPlayer = new ExoMediaPlayer(view.getContext());
//        exoMediaPlayer.setPlayWhenReady(true); // 自动播放
//        videoView.setMediaPlayer(exoMediaPlayer);
//
//        ExoSourceBuilder exoSourceBuilder = new ExoSourceBuilder(this, );
//        exoSourceBuilder.setCacheEnable(false);
//        MediaSource exoMediaSource = exoSourceBuilder.build();
//        exoMediaPlayer.setMediaSource();

        VideoView videoView = findViewById(R.id.videoView);
        Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.start_page_video);
        videoView.setVideoURI(videoUri);
        videoView.setOnCompletionListener(MediaPlayer::start);


        new Handler().postDelayed(() -> {
            Map<String, String> loginMessage = Application.getLoginMessage();
            Intent intent;
            String token = loginMessage.get("userToken");
            if(token == null){
                intent = new Intent(this, LoginActivity.class);
            }else {
                intent = new Intent(this, MainActivity.class);
            }
            startActivity(intent);
        },3000);
    }
}