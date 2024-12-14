package edu.ace.infinite.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import edu.ace.infinite.R;
import edu.ace.infinite.fragment.MessageFragment;
import edu.ace.infinite.fragment.PersonalFragment;
import edu.ace.infinite.pojo.User;
import edu.ace.infinite.utils.http.Config;
import edu.ace.infinite.utils.http.UserHttpUtils;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    private List<User> users;
    private Context context;

    public UserAdapter(Context context, List<User> users) {
        this.users = users;
        this.context = context;
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        public ImageView avatarImage;
        public TextView nicknameTextView;
        public TextView descTextView;
        public Button followButton;

        public UserViewHolder(View itemView) {
            super(itemView);
            avatarImage = itemView.findViewById(R.id.avatar_image);
            nicknameTextView = itemView.findViewById(R.id.nickname);
            descTextView = itemView.findViewById(R.id.desc);
            followButton = itemView.findViewById(R.id.button_follow);
        }
    }

    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(v);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBindViewHolder(UserViewHolder holder, int position) {
        User user = users.get(position);
        holder.nicknameTextView.setText(user.getNickname());

        String intro = user.getIntro();
        if(!intro.isEmpty()){
            holder.descTextView.setText(intro);
        }else {
            holder.descTextView.setText("暂无简介");
        }
//        holder.avatarImage.setImageResource(user.getAvatarResId());

        Glide.with(context).load(Config.BaseUrl+user.getAvatar()).into(holder.avatarImage);
        
        // 根据关注状态设置按钮文本和背景颜色
        if(user.isFollowed()){
            holder.followButton.setText("取消关注");
            holder.followButton.setBackground(context.getDrawable(R.drawable.button_unfollow));
        } else {
            holder.followButton.setText("关注");
            holder.followButton.setBackground(context.getDrawable(R.drawable.button_follow));
        }
        
        holder.followButton.setOnClickListener(v -> {
            boolean followed = user.isFollowed();
            if(followed){
                // 取消关注
                user.setFollowed(false);
                holder.followButton.setText("关注");
                holder.followButton.setBackground(context.getDrawable(R.drawable.button_follow));
            } else {
                // 关注
                user.setFollowed(true);
                holder.followButton.setText("取消关注");
                holder.followButton.setBackground(context.getDrawable(R.drawable.button_unfollow));
            }

            new Thread(() -> {
                boolean succeed = UserHttpUtils.followUser(user.getId(), !followed);
                if(!succeed){
                    holder.itemView.post(() -> {
                        if(!user.isFollowed()){
                            user.setFollowed(true);
                            holder.followButton.setText("取消关注");
                            holder.followButton.setBackground(context.getDrawable(R.drawable.button_unfollow));
                            Toast.makeText(holder.itemView.getContext(), "关注失败", Toast.LENGTH_SHORT).show();
                        }else {
                            user.setFollowed(false);
                            holder.followButton.setText("关注");
                            holder.followButton.setBackground(context.getDrawable(R.drawable.button_follow));
                            Toast.makeText(holder.itemView.getContext(), "取关失败", Toast.LENGTH_SHORT).show();
                        }
                    });
                }else {
                    MessageFragment.refreshFollowList = true;
                    PersonalFragment.refreshInfo = true;
                }
            }).start();
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }
} 