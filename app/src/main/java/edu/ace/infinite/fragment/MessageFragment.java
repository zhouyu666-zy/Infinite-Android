package edu.ace.infinite.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import edu.ace.infinite.R;
import edu.ace.infinite.activity.ChatActivity;
import edu.ace.infinite.adapter.MessageListAdapter;
import edu.ace.infinite.pojo.MessageListItem;


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
        // TODO: 从服务器加载消息列表
        // 这里先添加一些测试数据
        messageList.add(new MessageListItem(
            "1",
            "一只小皇帝",
            "",
            "1小时内在线",
            "31分钟前",
            true
        ));
        messageList.add(new MessageListItem(
            "2",
            "幻想过爱",
            "",
            "在线",
            "",
            true
        ));
        messageListAdapter.notifyDataSetChanged();
    }
}