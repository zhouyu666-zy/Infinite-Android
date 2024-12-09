package edu.ace.infinite.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import edu.ace.infinite.R;
import edu.ace.infinite.activity.VideoPlayActivity;
import edu.ace.infinite.pojo.Video;

import java.util.ArrayList;
import java.util.List;

public class PersonalVideoAdapter extends RecyclerView.Adapter<PersonalVideoAdapter.VideoViewHolder> {

    private List<Video.Data> videos = new ArrayList<>();
    private Context context;

    public PersonalVideoAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_video_grid, parent, false);
        return new VideoViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Video.Data video = videos.get(position);
        //加载预览图片
        Glide.with(context).load(video.getCoverSrc()).into(holder.imageView);
        //加载标题
        holder.textView.setText("@"+video.getNickname());

        holder.itemView.setOnClickListener(view -> {
            VideoPlayActivity.videoList = videos;
            VideoPlayActivity.position = position;
            Intent intent = new Intent(context, VideoPlayActivity.class);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return videos.size();
    }

    public void setVideos(List<Video.Data> videos) {
        this.videos = videos;
        notifyDataSetChanged();
    }

    static class VideoViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textView;

        VideoViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.video_thumbnail);
            textView = itemView.findViewById(R.id.video_title);
        }
    }
}
