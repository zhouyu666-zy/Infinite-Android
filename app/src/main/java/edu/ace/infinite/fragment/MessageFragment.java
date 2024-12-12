package edu.ace.infinite.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.orhanobut.hawk.Hawk;

import edu.ace.infinite.R;
import edu.ace.infinite.activity.ChatActivity;
import edu.ace.infinite.activity.SearchActivity;
import edu.ace.infinite.adapter.MessageListAdapter;
import edu.ace.infinite.pojo.MessageListItem;
import edu.ace.infinite.pojo.User;
import edu.ace.infinite.utils.http.UserHttpUtils;

import java.util.ArrayList;
import java.util.List;

public class MessageFragment extends BaseFragment {
    public static boolean refreshList = false;
    public static boolean refreshFollowList = false;
    private RecyclerView messageListRecyclerView;
    private MessageListAdapter messageListAdapter;
    private static List<MessageListItem> messageList;
    public static List<MessageListItem> getMessageList(){
        if(messageList == null){
            messageList = new ArrayList<>();
        }
        return messageList;
    }
    public static void setMessageList(List<MessageListItem> messageList){
          MessageFragment.messageList = messageList;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_message, container, false);
        initViews();

        loadMessages();
        refreshList();
        return view;
    }

    private void initViews() {
        View titleContainer = findViewById(R.id.titleContainer);
        if(titleContainer != null){
            ViewCompat.setOnApplyWindowInsetsListener(titleContainer, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }

        findViewById(R.id.user_search_btn).setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), SearchActivity.class);
            startActivity(intent);
        });

        messageListRecyclerView = view.findViewById(R.id.messageListRecyclerView);
        messageListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        messageList = new ArrayList<>();
        messageListAdapter = new MessageListAdapter(getContext(), messageList);
        messageListRecyclerView.setAdapter(messageListAdapter);
    }

    private boolean isLoad = false;
    @SuppressLint("NotifyDataSetChanged")
    private void loadMessages() {
        if(isLoad){
            return;
        }
        isLoad = true;
        new Thread(() -> {
            String token = Hawk.get("loginToken");
            List<User> followList = UserHttpUtils.getFollowList();
            for (User user : followList) {
                boolean isExist = false;
                String id = String.valueOf(user.getId()).trim();
                for (MessageListItem messageListItem : messageList) {
                    if(messageListItem.getUserId().trim().equals(id)){
                        isExist = true;
                        break;
                    }
                }
                if(!isExist){
                    MessageListItem messageListItem = MessageListItem.userToMessageItem(user, token);
                    messageList.add(messageListItem);
                }
            }
            getActivity().runOnUiThread(() -> messageListAdapter.notifyDataSetChanged());
            isLoad = false;
        }).start();
    }

    private void refreshList() {
        view.post(new Runnable() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void run() {
                if(refreshList){
                    refreshList = false;
                    messageListAdapter.notifyDataSetChanged();
                }
                if(refreshFollowList){
                    refreshFollowList = false;
                    loadMessages();
                }
                view.postDelayed(this, 500);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        refreshList = true;
    }

}