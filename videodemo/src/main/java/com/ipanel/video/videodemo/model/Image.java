package com.ipanel.video.videodemo.model;

public class Image {
    private Integer id;//海报图片Id

    private Integer videoid;//视频Id

    private String groupstring;//海报适用的所有规格场景,如：适用于1,2,3场景，将其存为字符串

    private String path;//海报的存储地址

    private String size;//海报所适用的尺寸

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getVideoid() {
        return videoid;
    }

    public void setVideoid(Integer videoid) {
        this.videoid = videoid;
    }

    public String getGroupstring() {
        return groupstring;
    }

    public void setGroupstring(String groupstring) {
        this.groupstring = groupstring == null ? null : groupstring.trim();
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path == null ? null : path.trim();
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size == null ? null : size.trim();
    }
}