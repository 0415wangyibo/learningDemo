package com.wangyb.ftpdemo.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with Intellij IDEA.
 *
 * @author wangyb
 * @Date 2019/3/27 15:50
 * Modified By:
 * Description:
 */
@Data
@ApiModel("下载上传历史")
public class DayDownLoadInfo implements Serializable {

    private static final long serialVersionUID = 7130250644216675184L;

    @ApiModelProperty(value = "下载名，唯一id，可以用日期字符串，即文件夹名")
    private String downName;
    @ApiModelProperty(value = "文件总下载大小，字节")
    private Long downLoadTotal;
    @ApiModelProperty(value = "已经下载的文件大小，字节")
    private Long loadedTotal;
    @ApiModelProperty(value = "文件总个数")
    private Integer fileNumber;
    @ApiModelProperty(value = "已下载文件个数")
    private Integer loadedFileNumber;
    @ApiModelProperty(value = "下载状态,0--未下载，1--下载中，-1--下载失败或登录服务器失败，2--核对文件中，-2--文件不完整，3——下载并核对文件成功,-3--文件不存在")
    private Integer status;
    @ApiModelProperty(value = "产生该状态的原因")
    private String reason;
    @ApiModelProperty(value = "缺少的文件路径，本地多出来的没管，非人为情况不会出现")
    private List<String> missPath;
    @ApiModelProperty(value = "发送邮件标识")
    private Integer sendTimes;
    @ApiModelProperty(value = "正在下载的文件")
    private String downloadNow;
    @ApiModelProperty(value = "正在下载的文件大小")
    private Long downloadNowSize;
    @ApiModelProperty(value = "文件总上传大小，字节")
    private Long uploadTotal;
    @ApiModelProperty(value = "已经上传的文件大小，字节")
    private Long uploadedTotal;
    @ApiModelProperty(value = "要上传文件总个数")
    private Integer uploadFileNumber;
    @ApiModelProperty(value = "已上传文件个数")
    private Integer uploadedFileNumber;
    @ApiModelProperty(value = "上传状态，0--未上传，1--上传中，-1--上传失败或者登陆服务器失败，2--核对文件中，-2--文件不完整，3--上传并核对文件成功,-3--本地文件夹不存在")
    private Integer uploadStatus;
    @ApiModelProperty(value = "产生该状态的原因")
    private String uploadReason;
    @ApiModelProperty(value = "上传文件的ftp中缺失文件的路径")
    private List<String> uploadMissPath;
    @ApiModelProperty(value = "正在上传的文件")
    private String uploadNow;
    @ApiModelProperty(value = "正在上传的文件大小")
    private Long uploadNowSize;

    //检查文件下载情况时构造方法
    public DayDownLoadInfo(DayDownLoadInfo dayDownLoadInfo){
        this.downName = dayDownLoadInfo.getDownName();
        this.downLoadTotal = dayDownLoadInfo.getDownLoadTotal();
        this.loadedTotal = dayDownLoadInfo.getLoadedTotal();
        this.fileNumber = dayDownLoadInfo.getFileNumber();
        this.loadedFileNumber = dayDownLoadInfo.getLoadedFileNumber();
        this.status = dayDownLoadInfo.getStatus();
        this.reason = dayDownLoadInfo.getReason();
        this.missPath = dayDownLoadInfo.getMissPath();
        this.sendTimes = dayDownLoadInfo.getSendTimes();
        this.downloadNow = dayDownLoadInfo.getDownloadNow();
        this.downloadNowSize = dayDownLoadInfo.getDownloadNowSize();
        this.uploadTotal = dayDownLoadInfo.getUploadTotal();
        this.uploadedTotal = dayDownLoadInfo.getUploadedTotal();
        this.uploadFileNumber = dayDownLoadInfo.getUploadFileNumber();
        this.uploadedFileNumber = dayDownLoadInfo.getUploadedFileNumber();
        this.uploadStatus = dayDownLoadInfo.getUploadStatus();
        this.uploadReason = dayDownLoadInfo.getUploadReason();
        this.uploadMissPath = dayDownLoadInfo.getUploadMissPath();
        this.uploadNow = dayDownLoadInfo.getUploadNow();
        this.uploadNowSize = dayDownLoadInfo.getUploadNowSize();
    }
    //下载构造方法
    public DayDownLoadInfo(String downName, Integer ifCheck) {
        this.downName = downName;
        this.downLoadTotal = 0L;
        this.loadedTotal = 0L;
        this.fileNumber = 0;
        this.loadedFileNumber = 0;
        this.status = 0;
        this.reason = "";
        this.missPath = new ArrayList<>();
        this.sendTimes = 0;
        this.uploadTotal = 0L;
        this.uploadedTotal = 0L;
        this.uploadFileNumber = 0;
        this.uploadedFileNumber = 0;
        this.uploadStatus = 0;
        this.uploadMissPath = new ArrayList<>();
        this.uploadReason = "";
        DayDownLoadInfo dayDownLoadInfo = JobCommon.JOB_COMMON.getDownNameSameInfo(downName);
        //下载时使用该方法构造
        if (null != dayDownLoadInfo && ifCheck == 0) {
            this.status = dayDownLoadInfo.getStatus();
            this.reason = dayDownLoadInfo.getReason();
            this.missPath = dayDownLoadInfo.getMissPath();
            this.sendTimes = dayDownLoadInfo.getSendTimes();
            this.uploadTotal = dayDownLoadInfo.getUploadTotal();
            this.uploadedTotal = dayDownLoadInfo.getUploadedTotal();
            this.uploadFileNumber = dayDownLoadInfo.getUploadFileNumber();
            this.uploadedFileNumber = dayDownLoadInfo.getUploadedFileNumber();
            this.uploadStatus = dayDownLoadInfo.getUploadStatus();
            this.uploadReason = dayDownLoadInfo.getUploadReason();
            this.uploadMissPath = dayDownLoadInfo.getUploadMissPath();
            this.uploadNow = dayDownLoadInfo.getUploadNow();
            this.uploadNowSize = dayDownLoadInfo.getUploadNowSize();
        }
        //检查时使用该构造方法
        if (null != dayDownLoadInfo && ifCheck == 1) {
            this.downLoadTotal = dayDownLoadInfo.getDownLoadTotal();
            this.loadedTotal = dayDownLoadInfo.getLoadedTotal();
            this.fileNumber = dayDownLoadInfo.getFileNumber();
            this.loadedFileNumber = dayDownLoadInfo.getLoadedFileNumber();
            this.status = dayDownLoadInfo.getStatus();
            this.reason = dayDownLoadInfo.getReason();
            this.missPath = dayDownLoadInfo.getMissPath();
            this.sendTimes = dayDownLoadInfo.getSendTimes();
            this.downloadNow = dayDownLoadInfo.getDownloadNow();
            this.downloadNowSize = dayDownLoadInfo.getDownloadNowSize();
            this.uploadTotal = dayDownLoadInfo.getUploadTotal();
            this.uploadedTotal = dayDownLoadInfo.getUploadedTotal();
            this.uploadFileNumber = dayDownLoadInfo.getUploadFileNumber();
            this.uploadedFileNumber = dayDownLoadInfo.getUploadedFileNumber();
            this.uploadStatus = dayDownLoadInfo.getUploadStatus();
            this.uploadReason = dayDownLoadInfo.getUploadReason();
            this.uploadMissPath = dayDownLoadInfo.getUploadMissPath();
            this.uploadNow = dayDownLoadInfo.getUploadNow();
            this.uploadNowSize = dayDownLoadInfo.getUploadNowSize();
        }
    }

    //上传构造方法
    public DayDownLoadInfo(String downName){
        this.downName = downName;
        this.downLoadTotal = 0L;
        this.loadedTotal = 0L;
        this.fileNumber = 0;
        this.loadedFileNumber = 0;
        this.status = 3;
        this.reason = "";
        this.missPath = new ArrayList<>();
        this.downloadNow = null;
        this.downloadNowSize = null;
        this.sendTimes = 2;
        this.uploadTotal = 0L;
        this.uploadedTotal = 0L;
        this.uploadFileNumber = 0;
        this.uploadedFileNumber = 0;
        this.uploadStatus = 0;
        this.uploadReason = "";
        this.uploadMissPath = new ArrayList<>();
        this.uploadNow = null;
        this.uploadNowSize = null;
    }
}
