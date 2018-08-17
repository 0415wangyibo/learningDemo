package com.ipanel.video.videodemo.controller.request;

public class ShowImg {
    private Integer videoId;//要展示海报的视频Id
    private Integer groupId;//要展示的海报的规格编号
    private Integer width;//要展示海报的尺寸宽度，320x400尺寸，240x300尺寸，160x200尺寸，500x280尺寸，375x210尺寸，246x138尺寸，182x102尺寸
    private Integer height;//要展示海报的尺寸高度

    public Integer getVideoId() {
        return videoId;
    }

    public void setVideoId(Integer videoId) {
        this.videoId = videoId;
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

}
