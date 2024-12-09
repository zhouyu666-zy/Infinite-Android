package edu.ace.infinite.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import edu.ace.infinite.R;
import edu.ace.infinite.adapter.MessageAdapter;
import edu.ace.infinite.model.Message;

import java.util.ArrayList;
import java.util.List;

public class MessageFragment extends BaseFragment{
    private RecyclerView recyclerViewMessages;
    private MessageAdapter messageAdapter;
    private List<Message> messageList; // 假设有一个Message模型类

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_message, container, false);
        initViews();
        return view;
    }

    private void initViews() {
        recyclerViewMessages = view.findViewById(R.id.recyclerView_messages);
        recyclerViewMessages.setLayoutManager(new LinearLayoutManager(getContext()));
        messageList = getMessages(); // 获取消息列表的方法
        messageAdapter = new MessageAdapter(messageList);
        recyclerViewMessages.setAdapter(messageAdapter);
    }

    private List<Message> getMessages() {
        List<Message> messages = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            messages.add(new Message("消息1", "发送者1", ""));
        }
        return messages;
    }
}
