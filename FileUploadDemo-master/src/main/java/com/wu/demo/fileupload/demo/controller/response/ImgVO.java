package com.wu.demo.fileupload.demo.controller.response;

import com.wu.demo.fileupload.demo.entity.Img;

public class ImgVO {
    private Integer videoId;
    private String path1;//320x400尺寸的海报路径
    private String path2;//240x300尺寸的海报路径
    private String path3;//160x200尺寸的海报路径
    private String path4;//500x280尺寸的海报路径
    private String path5;//375x210尺寸的海报路径
    private String path6;//246x138尺寸的海报路径
    private String path7;//182x102尺寸的海报路径

    public Integer getVideoId() {
        return videoId;
    }

    public void setVideoId(Integer videoId) {
        this.videoId = videoId;
    }

    public String getPath1() {
        return path1;
    }

    public void setPath1(String path1) {
        this.path1 = path1;
    }

    public String getPath2() {
        return path2;
    }

    public void setPath2(String path2) {
        this.path2 = path2;
    }

    public String getPath3() {
        return path3;
    }

    public void setPath3(String path3) {
        this.path3 = path3;
    }

    public String getPath4() {
        return path4;
    }

    public void setPath4(String path4) {
        this.path4 = path4;
    }

    public String getPath5() {
        return path5;
    }

    public void setPath5(String path5) {
        this.path5 = path5;
    }

    public String getPath6() {
        return path6;
    }

    public void setPath6(String path6) {
        this.path6 = path6;
    }

    public String getPath7() {
        return path7;
    }

    public void setPath7(String path7) {
        this.path7 = path7;
    }

    public ImgVO(Img img){
        this.videoId = img.getVideoId();
        this.path1 = img.getPath1();
        this.path2 = img.getPath2();
        this.path3 = img.getPath3();
        this.path4 = img.getPath4();
        this.path5 = img.getPath5();
        this.path6 = img.getPath6();
        this.path7 = img.getPath7();
    }

    public ImgVO(){

    }
}
