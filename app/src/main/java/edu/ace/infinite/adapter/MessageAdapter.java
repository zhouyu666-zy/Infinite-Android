package edu.ace.infinite.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import edu.ace.infinite.R;
import edu.ace.infinite.websocket.model.ChatMessage;

import java.util.List;

public class                                                                                                                                                                                                                                                              MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
    private List<ChatMessage> messages;

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageViewAvatar;
        public TextView textViewNickname;
        public TextView textViewMessagePreview;

        public MessageViewHolder(View itemView) {
            super(itemView);
            imageViewAvatar = itemView.findViewById(R.id.imageView_avatar);
            textViewNickname = itemView.findViewById(R.id.textView_nickname);
            textViewMessagePreview = itemView.findViewById(R.id.textView_message_preview);
        }
    }

    public MessageAdapter(List<ChatMessage> messages) {
        this.messages = messages;
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, parent, false);
        return new MessageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MessageViewHolder holder, int position) {
        // 这里可以根据需要加载头像图片，例如使用Glide或Picasso

    }

    @Override
    public int getItemCount() {
        return messages.size();
    }
} 