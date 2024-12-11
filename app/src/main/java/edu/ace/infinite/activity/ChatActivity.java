package edu.ace.infinite.activity;


import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.orhanobut.hawk.Hawk;

import java.util.ArrayList;
import java.util.List;

import edu.ace.infinite.R;
import edu.ace.infinite.adapter.MessageAdapter;
import edu.ace.infinite.application.WebSocketManager;
import edu.ace.infinite.fragment.MessageFragment;
import edu.ace.infinite.pojo.ChatMessage;
import edu.ace.infinite.pojo.MessageListItem;
import edu.ace.infinite.utils.TimeUtils;

public class ChatActivity extends AppCompatActivity implements WebSocketManager.MessageCallback {

    private RecyclerView recyclerViewChat;
    private EditText editTextMessage;
    private Button buttonSend;
    private MessageAdapter messageAdapter;
    public static MessageListItem messageListItem;
    private List<ChatMessage> chatMessages;
    private String currentUserId;
    private String currentUserName;
    private String targetUserId;
    private String targetUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // 获取传递的用户信息
        targetUserId = getIntent().getStringExtra("userId");
        targetUserName = getIntent().getStringExtra("username");
        currentUserId = Hawk.get("loginToken");
        currentUserName = "我"; // 可以从用户配置或登录信息中获取

        chatMessages = messageListItem.getChatMessageList();
        messageListItem.setUnreadCount(0); //重置未读数量

        // 设置标题
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(targetUserName);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        initViews();
        setupWebSocket();
        loadHistoryMessages();
    }
    private void initViews() {
        recyclerViewChat = findViewById(R.id.recyclerViewChat);
        editTextMessage = findViewById(R.id.editTextMessage);
        buttonSend = findViewById(R.id.buttonSend);

        messageAdapter = new MessageAdapter(chatMessages,currentUserId);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerViewChat.setLayoutManager(layoutManager);
        recyclerViewChat.setAdapter(messageAdapter);

        buttonSend.setOnClickListener(v -> sendMessage());
    }
    private void setupWebSocket() {
        WebSocketManager.getInstance().setMessageCallback(this);
    }
    private void loadHistoryMessages() {
        // 模拟加载历史消息
//        ChatMessage welcomeMsg = new ChatMessage("system", "系统", targetUserId,
//            "欢迎来到与 " + targetUserName + " 的聊天", 0);
//        chatMessages.add(welcomeMsg);
//
//         添加一些模拟的历史消息
//        addSimulatedMessage(targetUserId, targetUserName, "你好，很高兴认识你！");
//        addSimulatedMessage(currentUserId, currentUserName, "你好，我也很高兴认识你！");
        
        messageAdapter.notifyDataSetChanged();
        scrollToBottom();
    }

    private void addSimulatedMessage(String senderId, String senderName, String content) {
        ChatMessage message = new ChatMessage(senderId, senderName,
            senderId.equals(currentUserId) ? targetUserId : currentUserId,
            content, 1);
        chatMessages.add(message);
    }

    private void sendMessage() {
        String token = Hawk.get("loginToken");
        String messageText = editTextMessage.getText().toString().trim();
        if (!messageText.isEmpty()) {
            // 创建消息对象
            ChatMessage chatMessage = new ChatMessage(token, currentUserName,
                    targetUserId, messageText, 1);
            
            // 发送到WebSocket服务器
            String jsonMessage = new Gson().toJson(chatMessage);
            WebSocketManager.getInstance().sendMessage(jsonMessage);
            
            // 添加到本地消息列表
            chatMessages.add(chatMessage);
            messageAdapter.notifyItemInserted(chatMessages.size() - 1);
            scrollToBottom();

            messageListItem.setLastMessage(messageText);
            messageListItem.setLastTime(TimeUtils.friendlyTime(System.currentTimeMillis()));

            // 清空输入框
            editTextMessage.setText("");
        }
    }
    private void scrollToBottom() {
        recyclerViewChat.post(() -> 
            recyclerViewChat.smoothScrollToPosition(Math.max(0, chatMessages.size() - 1)));
    }
    @Override
    public void onMessage(String message) {
        runOnUiThread(() -> {
            //收到消息更新
//            ChatMessage chatMessage = new Gson().fromJson(message, ChatMessage.class);
//            chatMessages.add(chatMessage);
            messageListItem.setUnreadCount(0);
            messageAdapter.notifyItemInserted(chatMessages.size() - 1);
            scrollToBottom();
        });
    }

    @Override
    public void onConnected() {

    }

    @Override
    public void onDisconnected() {

    }

    @Override
    public void onError(String error) {

    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void handleMessage(String message) {
//        ChatMessage chatMessage = WebSocketManager.getInstance("ws://localhost:8181/chat").parseMessage(message);
//        chatMessages.add(chatMessage);
//        messageAdapter.notifyItemInserted(chatMessages.size() - 1);
//        recyclerViewChat.smoothScrollToPosition(chatMessages.size() - 1);
    }

    @Override
    protected void onDestroy() {
        WebSocketManager.getInstance().removeMessageCallback(this);
        chatMessages = new ArrayList<>();
        super.onDestroy();
    }

    @Override
    public void finish() {
        MessageFragment.refreshList = true;
        super.finish();
    }
}