package edu.ace.infinite.model;

public class VideoItem {
    private String title;
    private int thumbnailResId;

    public VideoItem(String title, int thumbnailResId) {
        this.title = title;
        this.thumbnailResId = thumbnailResId;
    }

    public String getTitle() {
        return title;
    }

    public int getThumbnailResId() {
        return thumbnailResId;
    }
}
