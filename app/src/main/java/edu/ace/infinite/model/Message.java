package edu.ace.infinite.model;

public class Message {
    private String nickname;
    private String preview;
    private String avatarUrl; // 如果使用网络图片

    // 构造函数
    public Message(String nickname, String preview, String avatarUrl) {
        this.nickname = nickname;
        this.preview = preview;
        this.avatarUrl = avatarUrl;
    }

    // Getter 和 Setter
    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPreview() {
        return preview;
    }

    public void setPreview(String preview) {
        this.preview = preview;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }
} 