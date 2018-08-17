package com.ipanel.video.videodemo.dto;

/**
 * 便于将尺寸转化为字符串
 */
public class ImgSize {
    private Integer width;
    private Integer height;

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

    public ImgSize(Integer width,Integer height){
        this.width = width;
        this.height = height;
    }

    public ImgSize(){

    }
}