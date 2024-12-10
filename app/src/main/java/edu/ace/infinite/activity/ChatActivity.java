package edu.ace.infinite.activity;

import static edu.ace.infinite.utils.http.VideoHttpUtils.userId;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import edu.ace.infinite.R;
import edu.ace.infinite.adapter.MessageAdapter;
import edu.ace.infinite.websocket.WebSocketManager;
import edu.ace.infinite.websocket.model.ChatMessage;

public class ChatActivity extends AppCompatActivity implements WebSocketManager.MessageCallback {

    private RecyclerView recyclerViewChat;
    private EditText editTextMessage;
    private Button buttonSend;
    private MessageAdapter messageAdapter;
    private List<ChatMessage> chatMessages = new ArrayList<>();
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
        currentUserId = userId; // 从VideoHttpUtils获取
        currentUserName = "我"; // 可以从用户配置或登录信息中获取

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

        messageAdapter = new MessageAdapter(chatMessages);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerViewChat.setLayoutManager(layoutManager);
        recyclerViewChat.setAdapter(messageAdapter);

        buttonSend.setOnClickListener(v -> sendMessage());
    }
    private void setupWebSocket() {
        String wsUrl = "ws://localhost:8181/chat/" + currentUserId;
        WebSocketManager.getInstance(wsUrl).setMessageCallback(this);
        WebSocketManager.getInstance(wsUrl).connect();
    }
    private void loadHistoryMessages() {
        // 模拟加载历史消息
        ChatMessage welcomeMsg = new ChatMessage("system", "系统", targetUserId, 
            "欢迎来到与 " + targetUserName + " 的聊天", 0);
        chatMessages.add(welcomeMsg);

        // 添加一些模拟的历史消息
        addSimulatedMessage(targetUserId, targetUserName, "你好，很高兴认识你！");
        addSimulatedMessage(currentUserId, currentUserName, "你好，我也很高兴认识你！");
        
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
        String messageText = editTextMessage.getText().toString().trim();
        if (!messageText.isEmpty()) {
            // 创建消息对象
            ChatMessage chatMessage = new ChatMessage(currentUserId, currentUserName,
                targetUserId, messageText, 1);
            
            // 发送到WebSocket服务器
            String jsonMessage = new Gson().toJson(chatMessage);
            WebSocketManager.getInstance(null).sendMessage(jsonMessage);
            
            // 添加到本地消息列表
            chatMessages.add(chatMessage);
            messageAdapter.notifyItemInserted(chatMessages.size() - 1);
            scrollToBottom();
            
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
            ChatMessage chatMessage = new Gson().fromJson(message, ChatMessage.class);
            chatMessages.add(chatMessage);
            messageAdapter.notifyItemInserted(chatMessages.size() - 1);
            scrollToBottom();
        });
    }

    @Override
    public void onConnected() {
        runOnUiThread(() -> 
            Toast.makeText(this, "连接成功", Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onDisconnected() {
        runOnUiThread(() -> 
            Toast.makeText(this, "连接断开", Toast.LENGTH_SHORT).show());                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 
    }

    @Override
    public void onError(String error) {
        runOnUiThread(() -> 
            Toast.makeText(this, "错误: " + error, Toast.LENGTH_SHORT).show());
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void handleMessage(String message) {
        ChatMessage chatMessage = WebSocketManager.getInstance("ws://localhost:8181/chat").parseMessage(message);
        chatMessages.add(chatMessage);
        messageAdapter.notifyItemInserted(chatMessages.size() - 1);
        recyclerViewChat.smoothScrollToPosition(chatMessages.size() - 1);
    }
}