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
import edu.ace.infinite.pojo.MessageListItem;
import edu.ace.infinite.websocket.model.ChatMessage;
import java.util.List;

public class MessageListAdapter extends RecyclerView.Adapter<MessageListAdapter.ViewHolder> {
    private List<MessageListItem> messageList;
    private Context context;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(MessageListItem item);
    }

    public MessageListAdapter(Context context, List<MessageListItem> messageList) {
        this.context = context;
        this.messageList = messageList;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_message_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MessageListItem item = messageList.get(position);
        holder.usernameText.setText(item.getUsername());
        holder.lastMessageText.setText(item.getLastMessage());
        holder.timeText.setText(item.getLastTime());

        // 设置在线状态
        holder.onlineIndicator.setVisibility(item.isOnline() ? View.VISIBLE : View.GONE);

        // 设置未读消息数
        if (item.getUnreadCount() > 0) {
            holder.unreadCountText.setVisibility(View.VISIBLE);
            holder.unreadCountText.setText(String.valueOf(item.getUnreadCount()));
        } else {
            holder.unreadCountText.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView avatarImage;
        TextView usernameText;
        TextView lastMessageText;
        TextView timeText;
        View onlineIndicator;
        TextView unreadCountText;

        ViewHolder(View itemView) {
            super(itemView);
            avatarImage = itemView.findViewById(R.id.avatarImage);
            usernameText = itemView.findViewById(R.id.usernameText);
            lastMessageText = itemView.findViewById(R.id.lastMessageText);
            timeText = itemView.findViewById(R.id.timeText);
            onlineIndicator = itemView.findViewById(R.id.onlineIndicator);
            unreadCountText = itemView.findViewById(R.id.unreadCountText);
        }
    }
}