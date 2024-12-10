package edu.ace.infinite.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import edu.ace.infinite.R;
import edu.ace.infinite.activity.ChatActivity;
import edu.ace.infinite.adapter.MessageListAdapter;
import edu.ace.infinite.pojo.MessageListItem;
import edu.ace.infinite.websocket.WebSocketManager;
import edu.ace.infinite.websocket.model.ChatMessage;

import java.util.ArrayList;
import java.util.List;

public class MessageFragment extends BaseFragment {
    private RecyclerView messageListRecyclerView;
    private MessageListAdapter messageListAdapter;
    private List<MessageListItem> messageList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_message, container, false);
        initViews();
        loadMessages();
        return view;
    }

    private void initViews() {
        messageListRecyclerView = view.findViewById(R.id.messageListRecyclerView);
        messageListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        
        messageList = new ArrayList<>();
        messageListAdapter = new MessageListAdapter(getContext(), messageList);
        messageListRecyclerView.setAdapter(messageListAdapter);

        messageListAdapter.setOnItemClickListener(item -> {
            // 跳转到聊天界面
            Intent intent = new Intent(getActivity(), ChatActivity.class);
            intent.putExtra("userId", item.getUserId());
            intent.putExtra("username", item.getUsername());
            intent.putExtra("avatar", item.getAvatar());
            startActivity(intent);
        });
    }

    private void loadMessages() {
        // 清空现有消息列表
        messageList.clear();

        // 添加模拟的聊天列表数据
        messageList.add(new MessageListItem(
            "user1",
            "张三",
            "https://example.com/avatar1.jpg",
            "你好，我看了你发的视频，很有意思！",
            "刚刚",
            true,
            2
        ));

        messageList.add(new MessageListItem(
            "user2",
            "李四",
            "https://example.com/avatar2.jpg",
            "周末一起去拍视频吧",
            "5分钟前",
            true,
            0
        ));

        messageList.add(new MessageListItem(
            "user3",
            "王五",
            "https://example.com/avatar3.jpg",
            "[图片]",
            "30分钟前",
            false,
            1
        ));

        messageList.add(new MessageListItem(
            "user4",
            "赵六",
            "https://example.com/avatar4.jpg",
            "好的，那就这么说定了",
            "2小时前",
            false,
            0
        ));

        messageList.add(new MessageListItem(
            "group1",
            "视频创作者交流群",
            "https://example.com/group1.jpg",
            "小王：大家快来看看我的新作品",
            "昨天",
            true,
            5
        ));

        // 通知适配器数据已更新
        messageListAdapter.notifyDataSetChanged();
    }
}