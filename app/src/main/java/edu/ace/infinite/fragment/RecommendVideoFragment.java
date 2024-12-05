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
import edu.ace.infinite.utils.ConsoleUtils;
import edu.ace.infinite.utils.http.VideoHttpUtils;
import edu.ace.infinite.view.video.OnViewPagerListener;
import edu.ace.infinite.view.video.PageLayoutManager;

public class RecommendVideoFragment extends BaseFragment {
    private BaseActivity activity;
    private VideoAdapter videoAdapter;
    public RecyclerView videoRecyclerView;

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
    private void initView() {
        new Thread(() -> {
            Video video = VideoHttpUtils.getRecommentVideo();
            activity.runOnUiThread(() -> {
                if(video != null){
                    initVideoRecyclerView(video.getData());
                }else {
                    activity.showToast("网络请求失败");
                }
            });
        }).start();
    }

    private void initVideoRecyclerView(List<Video.Data> videoList) {
        videoRecyclerView = findViewById(R.id.videoRecyclerView);
        //设置为0，可以保证同时只有3条视频在加载
        videoRecyclerView.setItemViewCacheSize(0); //一级缓存，设置item复用数量

        PageLayoutManager pageLayoutManager = new PageLayoutManager(activity, OrientationHelper.VERTICAL, false);
        videoRecyclerView.setLayoutManager(pageLayoutManager);
        videoAdapter = new VideoAdapter(videoList, activity, this);
        videoRecyclerView.setAdapter(videoAdapter);

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

            private boolean isLoadMore = false;
            @Override
            public void onPageSelected(int position, boolean isBottom) {
//                ConsoleUtils.logErr(TAG, "选中位置：" + position+",是否为底部："+isBottom);
                currentPosition = position;
                playVideo(position);
                if(isBottom){
                    if(!isLoadMore){
                        //视频为底部时，加载更多视频
                        new Thread(() -> {
                            isLoadMore = true;
                            Video video = VideoHttpUtils.getRecommentVideo();
                            if(video != null){
                                videoAdapter.addVideo(video.getData());
                            }
                            isLoadMore = false;
                        }).start();
                    }
                }
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
    public VideoAdapter.ViewHolder currViewHolder;
    private void playVideo(int position) {
        if (videoRecyclerView == null) return;
        RecyclerView.ViewHolder viewHolder = videoRecyclerView.findViewHolderForAdapterPosition(position);
        if (viewHolder instanceof VideoAdapter.ViewHolder) {
            VideoAdapter.ViewHolder videoViewHolder = (VideoAdapter.ViewHolder) viewHolder;
            videoViewHolder.playVideo();
            currViewHolder = videoViewHolder;
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
        if(currViewHolder != null && exitIsPlay){
            currViewHolder.videoView.start();
//            playVideo(currentPosition);
        }
    }
    @Override
    public void onPause() {
        super.onPause();
        if(currViewHolder != null){
            exitIsPlay = currViewHolder.isPlaying();
            currViewHolder.videoView.pause();
//            ConsoleUtils.logErr(exitIsPlay);
        }
//        pauseVideo(currentPosition);
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
