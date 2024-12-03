package edu.ace.infinite.fragment;


import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;


import java.util.List;

import edu.ace.infinite.R;
import edu.ace.infinite.activity.BaseActivity;
import edu.ace.infinite.adapter.VideoAdapter;
import edu.ace.infinite.pojo.Video;
import edu.ace.infinite.utils.http.VideoHttpUtils;
import edu.ace.infinite.view.video.OnViewPagerListener;
import edu.ace.infinite.view.video.PageLayoutManager;

public class RecommendVideoFragment extends BaseFragment {
    private BaseActivity activity;
    private VideoAdapter videoAdapter;
    private RecyclerView videoRecyclerView;
    private LinearSnapHelper snapHelper;

    public RecommendVideoFragment(BaseActivity activity){
        this.activity = activity;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_recommend_video, container, false);
        initView();
        return view;
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onStart() {
        super.onStart();

    }

    @SuppressLint("SetTextI18n")
    private void initView() {
        new Thread(() -> {
            Video video = VideoHttpUtils.getRecommentVideo();
            activity.runOnUiThread(() -> {
                if(video != null){
                    initVideoRecyclerView(video.getData());
                }else {
                    activity.showToast("出错了");
                }
            });
        }).start();
    }

    private void initVideoRecyclerView(List<Video.Data> videoList) {
        videoRecyclerView = findViewById(R.id.videoRecyclerView);
        videoRecyclerView.setItemViewCacheSize(1); //保留几条视频的播放信息

        PageLayoutManager pageLayoutManager = new PageLayoutManager(activity, OrientationHelper.VERTICAL, false);
        videoRecyclerView.setLayoutManager(pageLayoutManager);
        videoAdapter = new VideoAdapter(videoList,activity);
        videoRecyclerView.setAdapter(videoAdapter);

        pageLayoutManager.setOnViewPagerListener(new OnViewPagerListener() {
            @Override
            public void onPageRelease(boolean isNext, int position) {
                Log.e(TAG, "释放位置:" + position);
                pauseVideo(position);
            }

            @Override
            public void onPageSelected(int position, boolean isBottom) {
                Log.e(TAG, "选择位置:" + position + "，是否为底部:" + isBottom);
                playVideo(position);

                if(isBottom){
                    //视频为底部时，加载更多视频
                    new Thread(() -> {
                        Video video = VideoHttpUtils.getRecommentVideo();
                        videoAdapter.addVideo(video.getData());
                    }).start();
                }
            }
        });
    }


    private void playVideo(int position) {
        if (videoRecyclerView == null) return;
        RecyclerView.ViewHolder viewHolder = videoRecyclerView.findViewHolderForAdapterPosition(position);
        if (viewHolder instanceof VideoAdapter.ViewHolder) {
            VideoAdapter.ViewHolder videoViewHolder = (VideoAdapter.ViewHolder) viewHolder;
            videoViewHolder.playVideo();
        }
    }
    private void pauseVideo(int position) {
        if (videoRecyclerView == null) return;
        RecyclerView.ViewHolder viewHolder = videoRecyclerView.findViewHolderForAdapterPosition(position);
        if (viewHolder instanceof VideoAdapter.ViewHolder) {
            VideoAdapter.ViewHolder videoViewHolder = (VideoAdapter.ViewHolder) viewHolder;
            videoViewHolder.pauseVideo();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        view = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(view != null){
            view = null;
        }
    }
}
