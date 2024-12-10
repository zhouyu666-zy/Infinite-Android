package edu.ace.infinite.websocket;

import static edu.ace.infinite.utils.http.VideoHttpUtils.userId;

import android.util.Log;
import androidx.annotation.NonNull;

import com.google.gson.Gson;

import edu.ace.infinite.websocket.model.ChatMessage;
import okhttp3.*;
import java.util.concurrent.TimeUnit;

public class WebSocketManager {
    private static final String TAG = "WebSocketManager";
    private static WebSocketManager instance;
    private WebSocket webSocket;
    private final String wsUrl; // WebSocket服务器地址
    private MessageCallback messageCallback;

    public interface MessageCallback {
        void onMessage(String message);
        void onConnected();
        void onDisconnected();
        void onError(String error);
    }

    private WebSocketManager(String url) {
        this.wsUrl = url;
    }

    public static WebSocketManager getInstance(String url) {
        if (instance == null) {
            synchronized (WebSocketManager.class) {
                if (instance == null) {
                    instance = new WebSocketManager(url);
                }
            }
        }
        return instance;
    }

    public void setMessageCallback(MessageCallback callback) {
        this.messageCallback = callback;
    }

    public void connect() {
        OkHttpClient client = new OkHttpClient.Builder()
                .pingInterval(30, TimeUnit.SECONDS)
                .build();

        Request request = new Request.Builder()
                .url(wsUrl)
                .build();

        webSocket = client.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onOpen(@NonNull WebSocket webSocket, @NonNull Response response) {
                Log.d(TAG, "WebSocket连接成功");
                webSocket.send("{\"type\": \"JOIN\", \"senderId\": \"" + userId + "\"}");
                if (messageCallback != null) {
                    messageCallback.onConnected();
                }
            }

            @Override
            public void onMessage(@NonNull WebSocket webSocket, @NonNull String text) {
                Log.d(TAG, "收到消息: " + text);
                handleMessage(text);
                if (messageCallback != null) {
                    messageCallback.onMessage(text);
                }
            }

            @Override
            public void onClosing(@NonNull WebSocket webSocket, int code, @NonNull String reason) {
                webSocket.close(1000, null);
                if (messageCallback != null) {
                    messageCallback.onDisconnected();
                }
            }

            @Override
            public void onFailure(@NonNull WebSocket webSocket, @NonNull Throwable t, Response response) {
                Log.e(TAG, "WebSocket连接失败", t);
                if (messageCallback != null) {
                    messageCallback.onError(t.getMessage());
                }
            }
        });
    }

    public void sendMessage(String message) {
        if (webSocket != null) {
            webSocket.send(message);
        }
    }

    public void disconnect() {
        if (webSocket != null) {
            webSocket.close(1000, "正常关闭");
        }
    }

    private void handleMessage(String message) {
        // 解析消息并更新UI
        ChatMessage chatMessage = parseMessage(message);
        // 根据消息类型进行处理
        switch (chatMessage.getMessageType()) {
            case 1: // JOIN
                // 处理用户加入聊天室
                break;
            case 2: // CHAT
                // 更新UI以显示新消息
                break;
            case 3: // PRIVATE
                if(chatMessage.getReceiverId().equals(userId)) {
                    // 显示私聊消息
                }
                break;
        }
    }

    public ChatMessage parseMessage(String jsonMessage) {
        // 解析JSON字符串为ChatMessage对象
        Gson gson = new Gson();
        return gson.fromJson(jsonMessage, ChatMessage.class);
    }
}
