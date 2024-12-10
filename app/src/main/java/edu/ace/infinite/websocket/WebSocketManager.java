package edu.ace.infinite.websocket;

import android.util.Log;
import androidx.annotation.NonNull;
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
                if (messageCallback != null) {
                    messageCallback.onConnected();
                }
            }

            @Override
            public void onMessage(@NonNull WebSocket webSocket, @NonNull String text) {
                Log.d(TAG, "收到消息: " + text);
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
}