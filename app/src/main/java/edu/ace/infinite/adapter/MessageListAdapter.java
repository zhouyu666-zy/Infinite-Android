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
import edu.ace.infinite.R;
import edu.ace.infinite.activity.ChatActivity;
import edu.ace.infinite.pojo.MessageListItem;

import java.util.List;

public class MessageListAdapter extends RecyclerView.Adapter<MessageListAdapter.ViewHolder> {
    private List<MessageListItem> messageList;
    private Context context;

    public MessageListAdapter(Context context, List<MessageListItem> messageList) {
        this.context = context;
        this.messageList = messageList;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_message_list, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MessageListItem item = messageList.get(position);
        holder.usernameText.setText(item.getUsername());
        holder.lastMessageText.setText(item.getLastMessage());
        holder.timeText.setText(item.getLastTime());

        // 设置在线状态
        holder.onlineIndicator.setVisibility(item.isOnline() ? View.VISIBLE : View.GONE);

        int unreadCount = item.getUnreadCount();
        // 设置未读消息数
        if (unreadCount > 0) {
            holder.unreadCountText.setVisibility(View.VISIBLE);
            if(unreadCount > 99){
                holder.unreadCountText.setText("99+");
            }else {
                holder.unreadCountText.setText(String.valueOf(unreadCount));
            }
        } else {
            holder.unreadCountText.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            //跳转到聊天界面
            Intent intent = new Intent(context, ChatActivity.class);
            intent.putExtra("userId", item.getUserId());
            intent.putExtra("username", item.getUsername());
            intent.putExtra("avatar", item.getAvatar());
            ChatActivity.messageListItem = item;
            context.startActivity(intent);
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