package edu.ace.infinite.fragment;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import edu.ace.infinite.R;
import edu.ace.infinite.activity.BaseActivity;
import edu.ace.infinite.activity.VideoPlayActivity;
import edu.ace.infinite.adapter.VideoAdapter;
import edu.ace.infinite.pojo.Video;
import edu.ace.infinite.view.MyToast;
import edu.ace.infinite.view.video.OnViewPagerListener;
import edu.ace.infinite.view.video.PageLayoutManager;

public class VideoPlayFragment extends BaseFragment {
    private BaseActivity activity;
    private VideoAdapter videoAdapter;
    public RecyclerView videoRecyclerView;

    public VideoPlayFragment(){}

    public VideoPlayFragment(BaseActivity activity){
        this.activity = activity;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(activity == null){
            activity = (BaseActivity) getActivity();
        }
        view = inflater.inflate(R.layout.fragment_recommend_video, container, false);
        initView();
        return view;
    }


    @SuppressLint("SetTextI18n")
    private void initView() {
        if(VideoPlayActivity.videoList != null && !VideoPlayActivity.videoList.isEmpty()){
            ArrayList<Video.Data> data = new ArrayList<>(VideoPlayActivity.videoList);
            initVideoRecyclerView(data);
        }else {
            MyToast.show("出错了");
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initVideoRecyclerView(List<Video.Data> videoList) {
        videoRecyclerView = findViewById(R.id.videoRecyclerView);
        //设置为0，可以保证同时只有3条视频在加载
        videoRecyclerView.setItemViewCacheSize(0); //一级缓存，设置item复用数量

        PageLayoutManager pageLayoutManager = new PageLayoutManager(activity, OrientationHelper.VERTICAL, false);
        videoRecyclerView.setLayoutManager(pageLayoutManager);
        videoAdapter = new VideoAdapter(videoList, activity, videoRecyclerView,false);
        videoRecyclerView.setAdapter(videoAdapter);
        videoRecyclerView.scrollToPosition(VideoPlayActivity.position);
        currentPosition = VideoPlayActivity.position;

        //设置滑动后选中和释放的监听
        pageLayoutManager.setOnViewPagerListener(new OnViewPagerListener() {
            @Override
            public void onPageRelease(boolean isNext, int position) {
//                ConsoleUtils.logErr(TAG, "释放位置:" + position);
                pauseVideo(position);
                // 调用预加载方法
                if(isNext){
                    preloadVideos(true,position+1);
                }else {
                    preloadVideos(false,position-1);
                }
            }

            @Override
            public void onPageSelected(int position, boolean isBottom) {
//                ConsoleUtils.logErr(TAG, "选中位置：" + position+",是否为底部："+isBottom);
                currentPosition = position;
                playVideo(position);
            }
        });
    }

    private void preloadVideos(boolean isNext,int currentPosition) {
        int preloadPosition;
        if(isNext){  //如果切换到下一个视频，则预加载当前页面的下一条
            preloadPosition = currentPosition + 1;
        }else {  //如果是切换到上一个视频，则加载当前页面的上一条
            preloadPosition = currentPosition - 1;
        }
        if (preloadPosition >= 0 && preloadPosition < videoAdapter.getItemCount()) {
            videoAdapter.preloadVideo(preloadPosition);
        }
    }

    private int currentPosition = 0;
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

    private boolean exitIsPlay;
    @SuppressLint("SetTextI18n")
    @Override
    public void onStart() {
        super.onStart();
        if(VideoAdapter.currPlayViewHolder != null && exitIsPlay){
            VideoAdapter.currPlayViewHolder.playVideo();
        }
    }
    @Override
    public void onPause() {
        super.onPause();
        if(VideoAdapter.currPlayViewHolder != null){
            exitIsPlay = VideoAdapter.currPlayViewHolder.isPlaying();
            VideoAdapter.currPlayViewHolder.pauseVideo();
        }
    }


    @Override
    public void onDestroyView() {
        VideoAdapter.currPlayViewHolder = null;
        super.onDestroyView();
        view = null;
    }

    @Override
    public void onDestroy() {
        VideoAdapter.currPlayViewHolder = null;
        super.onDestroy();
        if(view != null){
            view = null;
        }
    }
}
