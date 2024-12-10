package edu.ace.infinite.pojo;

public class MessageListItem {
    private String userId;
    private String username;
    private String avatar;
    private String lastMessage;
    private String lastTime;
    private boolean online;
    private int unreadCount;

    public MessageListItem(String userId, String username, String avatar, String lastMessage,
                           String lastTime, boolean online) {
        this.userId = userId;
        this.username = username;
        this.avatar = avatar;
        this.lastMessage = lastMessage;
        this.lastTime = lastTime;
        this.online = online;
        this.unreadCount = 0;
    }

    // Getters and Setters
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }

    public String getLastMessage() { return lastMessage; }
    public void setLastMessage(String lastMessage) { this.lastMessage = lastMessage; }

    public String getLastTime() { return lastTime; }
    public void setLastTime(String lastTime) { this.lastTime = lastTime; }

    public boolean isOnline() { return online; }
    public void setOnline(boolean online) { this.online = online; }

    public int getUnreadCount() { return unreadCount; }
    public void setUnreadCount(int unreadCount) { this.unreadCount = unreadCount; }
}
