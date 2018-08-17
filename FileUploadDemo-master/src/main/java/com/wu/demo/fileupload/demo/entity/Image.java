package com.wu.demo.fileupload.demo.entity;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "image")
public class Image implements Serializable{
    private static final long serialVersionUID = 5209922163861849058L;

    @Id
    @GeneratedValue
    private Integer id;//海报图片Id
    @Column
    private Integer videoId;//视频Id
    private String groupsString;//海报适用的所有规格场景,如：适用于1,2,3场景，将其存为字符串
    private String path;//海报的存储地址
    private String state;//默认任意规格适用该图片，规定：-1——只有在指定规格尺寸时适用该海报，0——任意规格尺寸在没有指定时使用该海报，1,2,3等——在对应规格下未指定尺寸时使用该海报
    private String size;//海报所适用的尺寸

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getVideoId() {
        return videoId;
    }

    public void setVideoId(Integer videoId) {
        this.videoId = videoId;
    }

    public String getGroupsString() {
        return groupsString;
    }

    public void setGroupsString(String groupsString) {
        this.groupsString = groupsString;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }
    public Image(){

    }
}
