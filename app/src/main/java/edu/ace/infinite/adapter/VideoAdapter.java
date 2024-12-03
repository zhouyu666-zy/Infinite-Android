package edu.ace.infinite.adapter;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.source.MediaSource;

import org.salient.artplayer.exo.ExoMediaPlayer;
import org.salient.artplayer.exo.ExoSourceBuilder;
import org.salient.artplayer.ui.VideoView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import edu.ace.infinite.R;
import edu.ace.infinite.activity.BaseActivity;
import edu.ace.infinite.application.Application;
import edu.ace.infinite.pojo.Video;
import edu.ace.infinite.utils.ConsoleUtils;
import edu.ace.infinite.utils.videoCache.HttpProxyCacheServer;
import edu.ace.infinite.view.CircleImage;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.ViewHolder>{
    private final List<Video.Data> videoList;
    private BaseActivity activity;

    public VideoAdapter(List<Video.Data> diaryList, BaseActivity activity) {
        this.videoList = diaryList;
        this.activity = activity;
    }

    public List<Video.Data> getVideoList() {
        return videoList;
    }

    public void addVideo(List<Video.Data> videos){
        int size = videoList.size();
        videoList.addAll(videos);
        activity.runOnUiThread(() -> notifyItemInserted(size));
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public VideoView videoView;
        public ExoMediaPlayer exoMediaPlayer;
        private boolean isPlay;
        private TextView video_title;
        private TextView author_nickname;
        private CircleImage author_avatar;



        public ViewHolder(View view) {
            super(view);

            author_nickname = view.findViewById(R.id.author_nickname);
            video_title = view.findViewById(R.id.video_title);
            author_avatar = view.findViewById(R.id.author_avatar);

            videoView = view.findViewById(R.id.videoView);
            exoMediaPlayer = new ExoMediaPlayer(view.getContext());
            exoMediaPlayer.setPlayWhenReady(false); // 加载完成不自动播放
            videoView.setMediaPlayer(exoMediaPlayer);

            exoMediaPlayer.getImpl().addListener(new Player.EventListener() {
                @Override
                public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                    switch(playbackState) {
                        case Player.STATE_BUFFERING:
                            // 播放器缓冲中
                            break;
                        case Player.STATE_READY:
                            if(isPlay){
                                videoView.start();
                            }
                            break;
                        case Player.STATE_ENDED:
                            // 播放结束
                            break;
                    }
                }

                @Override
                public void onPlayerError(ExoPlaybackException error) {
                    error.printStackTrace();
                    Player.EventListener.super.onPlayerError(error);
                }
            });
        }

        public void playVideo() {
            isPlay = true;
            videoView.start();
        }

        public void pauseVideo() {
            isPlay = false;
            videoView.pause();
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Video.Data video = videoList.get(position);
        //如果为第一条视频，设置加载完成后自动播放
        if(position == 0){
            holder.exoMediaPlayer.setPlayWhenReady(true);
        }

        holder.author_nickname.setText("@"+video.getNickname());
        holder.video_title.setText(video.getDesc());
        Glide.with(activity).load(video.getAuthorAvatar()).into(holder.author_avatar);
        try {
            HttpProxyCacheServer proxy = Application.getProxy();
            String proxyUrl = proxy.getProxyUrl(video.getVideoSrc(), video.getVideoId());
            ExoSourceBuilder exoSourceBuilder = new ExoSourceBuilder(holder.itemView.getContext(), proxyUrl);
            exoSourceBuilder.setCacheEnable(false);
            MediaSource exoMediaSource = exoSourceBuilder.build();
            holder.exoMediaPlayer.setMediaSource(exoMediaSource);
            holder.exoMediaPlayer.setLooping(true);
            if(holder.videoView != null){
                holder.videoView.prepare();
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }


    @Override
    public int getItemCount() {
        return videoList.size();
    }


}
