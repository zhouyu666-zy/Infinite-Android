package edu.ace.infinite.application;

import android.util.Log;
import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.orhanobut.hawk.Hawk;

import edu.ace.infinite.utils.ConsoleUtils;
import edu.ace.infinite.utils.http.Config;
import edu.ace.infinite.pojo.ChatMessage;
import okhttp3.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class WebSocketManager {
    private static final String TAG = "WebSocketManager";
    private static WebSocketManager instance;
    private WebSocket webSocket;
    private final String wsUrl = "ws://"+ Config.IP +"/message"; // WebSocket服务器地址
    private List<MessageCallback> messageCallbacks = new ArrayList<>();

    public interface MessageCallback {
        void onMessage(String message);
        void onConnected();
        void onDisconnected();
        void onError(String error);
    }

    private WebSocketManager() {}

    public static WebSocketManager getInstance() {
        if (instance == null) {
            instance = new WebSocketManager();
        }
        return instance;
    }

    public void setMessageCallback(MessageCallback callback) {
        this.messageCallbacks.add(callback);
    }
    public void removeMessageCallback(MessageCallback callback) {
        this.messageCallbacks.remove(callback);
    }

    public void connect() {
        OkHttpClient client = new OkHttpClient.Builder()
                .pingInterval(30, TimeUnit.SECONDS)
                .build();

        String token = Hawk.get("loginToken");

        Request request = new Request.Builder()
                .url(wsUrl+"?token=" + token)
                .build();

        webSocket = client.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onOpen(@NonNull WebSocket webSocket, @NonNull Response response) {
                ConsoleUtils.logErr("WebSocket连接成功");
                for (MessageCallback messageCallback : messageCallbacks) {
                    if (messageCallback != null) {
                        messageCallback.onConnected();
                    }
                }
            }
            @Override
            public void onMessage(@NonNull WebSocket webSocket, @NonNull String text) {
                Log.d(TAG, "收到消息: " + text);
                handleMessage(text);
                for (MessageCallback messageCallback : messageCallbacks) {
                    if (messageCallback != null) {
                        messageCallback.onMessage(text);
                    }
                }
            }

            @Override
            public void onClosing(@NonNull WebSocket webSocket, int code, @NonNull String reason) {
                webSocket.close(1000, null);
                for (MessageCallback messageCallback : messageCallbacks) {
                    if (messageCallback != null) {
                        messageCallback.onDisconnected();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull WebSocket webSocket, @NonNull Throwable t, Response response) {
                Log.e(TAG, "WebSocket连接失败", t);
                for (MessageCallback messageCallback : messageCallbacks) {
                    if (messageCallback != null) {
                        messageCallback.onError(t.getMessage());
                    }
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

        ConsoleUtils.logErr(message);

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
//                if(chatMessage.getReceiverId().equals(userId)) {
//                    // 显示私聊消息
//                }
                break;
        }
    }

    public ChatMessage parseMessage(String jsonMessage) {
        // 解析JSON字符串为ChatMessage对象
        Gson gson = new Gson();
        return gson.fromJson(jsonMessage, ChatMessage.class);
    }
}
