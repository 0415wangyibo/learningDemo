package com.wu.demo.fileupload.demo.controller.request;

import java.util.List;

public class AddImg {
    /**
     * 1.如果只传入videoId，则表示用该图片适配所有规格所有尺寸的海报
     * 2.如果只传入videoID、groupIds，则表示用该图片适配对应规格下的所有尺寸的海报
     * 3.如果传入所有参数，则表示用该图片适配指定规格、指定尺寸的海报
     */
    private Integer videoId;//要更改海报的视频Id
    private List<Integer> groupIds;//要更改的海报的规格编号
    private List<Integer> widths;//要更改海报的尺寸宽度，1-320x400尺寸，2-240x300尺寸，3-160x200尺寸，4-500x280尺寸，5-375x210尺寸，6-246x138尺寸，7-182x102尺寸
    private List<Integer> heights;//要更改海报的尺寸高度

    public Integer getVideoId() {
        return videoId;
    }

    public void setVideoId(Integer videoId) {
        this.videoId = videoId;
    }

    public List<Integer> getGroupIds() {
        return groupIds;
    }

    public void setGroupIds(List<Integer> groupIds) {
        this.groupIds = groupIds;
    }

    public List<Integer> getWidths() {
        return widths;
    }

    public void setWidths(List<Integer> widths) {
        this.widths = widths;
    }

    public List<Integer> getHeights() {
        return heights;
    }

    public void setHeights(List<Integer> heights) {
        this.heights = heights;
    }
}
