package edu.ace.infinite.pojo;

public class Like {
    private String user_id;
    private Video.Data video;
    private boolean isLike;

    public Like(String user_id, Video.Data video, boolean isLike) {
        this.user_id = user_id;
        this.video = video;
        this.isLike = isLike;
    }
}
