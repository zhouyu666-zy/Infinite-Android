package edu.ace.infinite.activity;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;
import java.util.List;

import edu.ace.infinite.R;
import edu.ace.infinite.fragment.RecommendVideoFragment;
import edu.ace.infinite.fragment.VideoPlayFragment;
import edu.ace.infinite.pojo.Video;

public class VideoPlayActivity extends BaseActivity {
    public static List<Video.Data> videoList;
    public static int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_video_play);
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(getColor(R.color.black));
        setStatusBarTextColor(false);

        VideoPlayFragment videoPlayFragment = new VideoPlayFragment(this);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_view,videoPlayFragment);
        fragmentTransaction.commit();
    }

    @Override
    protected void onDestroy() {
        position = 0;
        super.onDestroy();
    }
}