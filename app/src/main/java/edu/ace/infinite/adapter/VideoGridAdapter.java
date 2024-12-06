package edu.ace.infinite.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import edu.ace.infinite.R;
import edu.ace.infinite.model.VideoItem;
import java.util.ArrayList;
import java.util.List;

public class VideoGridAdapter extends RecyclerView.Adapter<VideoGridAdapter.VideoViewHolder> {

    private List<VideoItem> videos = new ArrayList<>();
    private Context context;

    public VideoGridAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_video_grid, parent, false);
        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
        VideoItem video = videos.get(position);
        holder.imageView.setImageResource(video.getThumbnailResId());
        holder.textView.setText(video.getTitle());
    }

    @Override
    public int getItemCount() {
        return videos.size();
    }

    public void setVideos(List<VideoItem> videos) {
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
