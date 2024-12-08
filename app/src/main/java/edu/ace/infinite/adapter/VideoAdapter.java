package edu.ace.infinite.adapter;

import android.annotation.SuppressLint;
import android.graphics.Rect;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


//import com.bumptech.glide.Glide;
import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;

import org.salient.artplayer.exo.ExoMediaPlayer;
import org.salient.artplayer.exo.ExoSourceBuilder;
import org.salient.artplayer.ui.VideoView;

import java.util.List;

import edu.ace.infinite.R;
import edu.ace.infinite.activity.BaseActivity;
import edu.ace.infinite.activity.MainActivity;
import edu.ace.infinite.activity.VideoPlayActivity;
import edu.ace.infinite.application.Application;
import edu.ace.infinite.pojo.Video;
import edu.ace.infinite.utils.ConsoleUtils;
import edu.ace.infinite.utils.http.VideoHttpUtils;
import edu.ace.infinite.utils.videoCache.HttpProxyCacheServer;
import edu.ace.infinite.view.CircleImage;
import edu.ace.infinite.view.EventUtils;
import edu.ace.infinite.view.LoveView;
import edu.ace.infinite.view.MyToast;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.ViewHolder> {
    private final List<Video.Data> videoList;
    private BaseActivity activity;
    private RecyclerView recyclerView;
    private boolean isRecommend;

    public VideoAdapter(List<Video.Data> diaryList, BaseActivity activity, RecyclerView recyclerView,boolean isRecommend) {
        this.videoList = diaryList;
        this.activity = activity;
        this.recyclerView = recyclerView;
        this.isRecommend = isRecommend;

        //解决RecyclerView滑动子视图获取不到事件的问题
        recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                if(e.getAction() == MotionEvent.ACTION_UP){
                    //抬起时如果视频还处于长按加速状态，则恢复倍速
                    if(isRecommend){
                        if(currMainPlayViewHolder.isLongPress){
                            currMainPlayViewHolder.setPlaySpeed(1.0f);
                            currMainPlayViewHolder.isLongPress = false;
                        }
                    }else {
                        if(currPlayViewHolder.isLongPress){
                            currPlayViewHolder.setPlaySpeed(1.0f);
                            currPlayViewHolder.isLongPress = false;
                        }
                    }

                }
                return false;
            }
            @Override
            public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {}
            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });
    }

    public List<Video.Data> getVideoList() {
        return videoList;
    }

    public void addVideo(List<Video.Data> videos) {
        int size = videoList.size();
        videoList.addAll(videos);
        activity.runOnUiThread(() -> notifyItemInserted(size));
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
        holder.reset();
        holder.isRecommend = isRecommend;
        Video.Data video = videoList.get(position);
        holder.video = video;
        holder.video_seekBar.setProgress(0);
        holder.video_play_image.setVisibility(View.GONE);

        boolean isFirst = !isRecommend && position == VideoPlayActivity.position;
        if(position == 0 || isFirst){
            holder.exoMediaPlayer.setPlayWhenReady(true); //第一条视频允许自动播放
            holder.playVideo();
            preloadVideo(position+1); //预加载下一条视频
        }

        holder.author_nickname.setText("@" + video.getNickname());
        holder.video_title.setText(video.getDesc());
        Glide.with(activity).load(video.getAuthorAvatar()).into(holder.author_avatar);

        if(video.isLike()){
            holder.like_btn.setImageTintList(null);
        }else {
            holder.like_btn.setImageTintList(holder.itemView.getContext().getColorStateList(R.color.white));
        }
        holder.like_btn.setOnClickListener(view -> {
            if(video.isLike()){
                holder.like_btn.setImageTintList(holder.itemView.getContext().getColorStateList(R.color.white));
                video.setLike(false);
                new Thread(() -> {
                    if(VideoHttpUtils.likeVideo(false,video)){
                        video.setLike(false);
                    }else {
                        video.setLike(true);
                        holder.itemView.post(() -> MyToast.show("取消点赞失败"));
                    }
                }).start();
            }else {
                holder.like_btn.setImageTintList(null);
                video.setLike(true);
                new Thread(() -> {
                    if(VideoHttpUtils.likeVideo(true,video)){
                        video.setLike(true);
                    }else {
                        video.setLike(false);
                        holder.itemView.post(() -> MyToast.show("点赞失败"));
                    }
                }).start();
            }
        });
        loadVideo(holder, video);
    }

    //加载视频
    private void loadVideo(ViewHolder holder, Video.Data video) {
        try {
            String videoId = video.getVideoId();
            //不是同一视频执行加载
            if (!holder.videoId.equals(videoId) || !holder.isInitializeComplete) {
                if (!holder.videoId.equals("null")) {
                    holder.reset();
                }
                holder.videoId = videoId;
                HttpProxyCacheServer proxy = Application.getProxy();
                String videoSrc = video.getVideoSrc();
                String proxyUrl = proxy.getProxyUrl(videoSrc, videoId);
                ExoSourceBuilder exoSourceBuilder = new ExoSourceBuilder(holder.itemView.getContext(), proxyUrl);
                exoSourceBuilder.setCacheEnable(false);
                MediaSource exoMediaSource = exoSourceBuilder.build();
                holder.exoMediaPlayer.setMediaSource(exoMediaSource);
                if (holder.videoView != null) {
                    holder.videoView.prepare();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //预加载
    public void preloadVideo(int position) {
        if (position < 0 || position >= videoList.size()) return;
        RecyclerView.ViewHolder viewHolder = findViewHolderForPosition(position);
        if (viewHolder instanceof ViewHolder) {
            ViewHolder holder = (ViewHolder) viewHolder;
            Video.Data video = videoList.get(position);
            loadVideo(holder, video);
        }
    }


    private RecyclerView.ViewHolder findViewHolderForPosition(int position) {
        return recyclerView.findViewHolderForAdapterPosition(position);
    }


    @Override
    public int getItemCount() {
        return videoList.size();
    }

    @SuppressLint("StaticFieldLeak")
    public static ViewHolder currMainPlayViewHolder = null;
    @SuppressLint("StaticFieldLeak")
    public static ViewHolder currPlayViewHolder = null;
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public VideoView videoView;
        public ExoMediaPlayer exoMediaPlayer;
        private Video.Data video;
        private boolean isPlay;
        private TextView video_title;
        private TextView author_nickname;
        private CircleImage author_avatar;
        private SeekBar video_seekBar;
        private View seekBarParent;
        private LoveView loveView;
        private ImageView video_play_image;
        private ImageView like_btn;
        private ImageView comment_btn;
        public boolean isInitializeComplete = false;
        private String videoId = "null";
        // 进度条是否在拖动
        private boolean isSeekBarTouch = false;
        private final int seekBarMax = 1000;
        public boolean isLongPress = false;
        private boolean isRecommend;

        public ViewHolder(View view) {
            super(view);
            author_nickname = view.findViewById(R.id.author_nickname);
            video_title = view.findViewById(R.id.video_title);
            author_avatar = view.findViewById(R.id.author_avatar);
            video_seekBar = view.findViewById(R.id.video_seekBar);
            seekBarParent = view.findViewById(R.id.seekBar_parent);
            video_play_image = view.findViewById(R.id.video_play_image);
            like_btn = view.findViewById(R.id.like_btn);
            comment_btn = view.findViewById(R.id.comment_btn);
            video_seekBar.setMax(seekBarMax);

            loveView = view.findViewById(R.id.loveView);

            videoView = view.findViewById(R.id.videoView);
            exoMediaPlayer = new ExoMediaPlayer(view.getContext());
            exoMediaPlayer.setPlayWhenReady(false); // 加载完成不自动播放
            videoView.setMediaPlayer(exoMediaPlayer);
            exoMediaPlayer.setLooping(true);
            exoMediaPlayer.getImpl().addListener(new Player.EventListener() {
                @Override
                public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                    switch (playbackState) {
                        case Player.STATE_BUFFERING:
                            // 播放器缓冲中
                            break;
                        case Player.STATE_READY:
                            //如果已经调用播放方法，则直接播放
                            isInitializeComplete = true;
                            if (isPlay) {
                                playVideo();
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

            // 添加点击、双击、长按事件监听
            setupGestureListeners(view);
            initSeekBar(); //初始化进度条
        }

        @SuppressLint("ClickableViewAccessibility")
        private void initSeekBar() {
            video_seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                }
                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    isSeekBarTouch = true;
                    // 手指开始触摸 SeekBar，放大进度条
                    seekBar.animate()
                            .scaleY(5f).setDuration(200).start();
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    int progress = seekBar.getProgress();
                    long duration = exoMediaPlayer.getDuration();
                    long newPosition = duration / seekBarMax * progress;
                    exoMediaPlayer.seekTo(newPosition); //修改播放器进度

                    // 手指停止触摸 SeekBar，恢复进度条大小
                    seekBar.animate()
                            .scaleY(1.0f).setDuration(200).start();
                    isSeekBarTouch = false;
                }
            });
//            video_seekBar.setOnTouchListener((v, event) -> {
//                // 让 SeekBar 不拦截触摸事件，让 GestureDetector 可以同时处理
//                return false;
//            });

            //增加seekbar触摸区域
            seekBarParent.setOnTouchListener((v, event) -> {
                Rect seekRect = new Rect();
                video_seekBar.getHitRect(seekRect);
                if ((event.getY() >= (seekRect.top - 500)) && (event.getY() <= (seekRect.bottom + 500))) {
                    float y = seekRect.top + (seekRect.height() >> 1);
                    //seekBar only accept relative x
                    float x = event.getX() - seekRect.left;
                    if (x < 0) {
                        x = 0;
                    } else if (x > seekRect.width()) {
                        x = seekRect.width();
                    }
                    MotionEvent me = MotionEvent.obtain(event.getDownTime(), event.getEventTime(),
                            event.getAction(), x, y, event.getMetaState());
                    return video_seekBar.onTouchEvent(me);
                }
                return false;
            });
        }

        private void setupGestureListeners(View view) {
            view.setOnTouchListener(new EventUtils.OnDoubleClickListener(new EventUtils.OnDoubleClickListener.DoubleClickCallback() {
                @Override
                public void onDoubleClick(MotionEvent event) {
                    handleDoubleTap(event);
                }

                @Override
                public void onClick(MotionEvent event) {
                    // 单击事件逻辑，切换播放/暂停
                    if (isPlaying()) {
                        pauseVideo();
                        // 缩小动画
                        video_play_image.setVisibility(View.VISIBLE);
                        video_play_image.setScaleX(1.5f);
                        video_play_image.setScaleY(1.5f);
                        video_play_image.setAlpha(0.6f);
                        video_play_image.animate()
                                .scaleX(1f)
                                .scaleY(1f)
                                .setDuration(100)
                                .withEndAction(null);
                    } else {
                        playVideo();
                        // 淡出动画
                        video_play_image.animate()
                                .alpha(0f)
                                .setDuration(200)
                                .withEndAction(() -> video_play_image.setVisibility(View.GONE));
                    }
                }

                @Override
                public void onLongPress(MotionEvent event) {
                    // 长按开始逻辑，例如加速播放
                    setPlaySpeed(3.0f);
                    isLongPress = true;
                }

                @Override
                public void onLongPressFinish(MotionEvent event) {
                    // 长按结束逻辑，例如恢复正常播放速度
                    setPlaySpeed(1.0f);
                    isLongPress = false;
                }
            }, itemView.getContext()));
        }

        // 双击事件处理方法
        private void handleDoubleTap(MotionEvent event) {
            loveView.addLoveView(event);
            ConsoleUtils.logErr("双击" + System.currentTimeMillis());
            if(!video.isLike()){
                video.setLike(true);
                like_btn.setImageTintList(null);
                new Thread(() -> {
                    if(VideoHttpUtils.likeVideo(true,video)){
                        video.setLike(true);
                    }else {
                        video.setLike(false);
                        itemView.post(() -> MyToast.show("点赞失败"));
                    }
                }).start();
            }
        }

        public boolean isPlaying() {
            return videoView.isPlaying();
        }

        private final Handler timeHandler = new Handler();
        public void playVideo() {
            isPlay = true;
            if(isRecommend){
                currMainPlayViewHolder = this;
            }else {
                currPlayViewHolder = this;
            }
            if(isInitializeComplete){
                exoMediaPlayer.start();
            }
            video_play_image.setVisibility(View.GONE);
            timeHandler.post(new Runnable() {
                @Override
                public void run() {
                    if(isInitializeComplete && !isSeekBarTouch){
                        long duration = exoMediaPlayer.getDuration();
                        long currentPosition = exoMediaPlayer.getCurrentPosition();
                        int progress = 0;
                        if (duration > 0) {
                            progress = (int) (currentPosition / ((double) duration / seekBarMax));
                        }
                        video_seekBar.setProgress(progress);
                    }
                    timeHandler.postDelayed(this,300);
                }
            });
        }

        public void pauseVideo() {
            isPlay = false;
            exoMediaPlayer.pause();
            timeHandler.removeCallbacksAndMessages(null);
        }

        public void reset() {
            isPlay = false;
            isInitializeComplete = false;
            exoMediaPlayer.reset();
        }

        // 设置播放速度的方法
        private void setPlaySpeed(float speed) {
            if (speed <= 0f) {
                speed = 1f;
            }
            SimpleExoPlayer impl = exoMediaPlayer.getImpl();
            PlaybackParameters playbackParameters = new PlaybackParameters(speed, 1.0F);
            impl.setPlaybackParameters(playbackParameters);
        }
    }
}
