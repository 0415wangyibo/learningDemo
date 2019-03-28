package com.wangyb.ftpdemo.config;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with Intellij IDEA.
 *
 * @author wangyb
 * @Date 2019/3/12 9:28
 * Modified By:
 * Description:
 */
public enum StatisticsCommon {

    STATISTICS_COMMON;

    //任务名
    private String downName;
    //下载状态
    private Integer downloadStatus;
    //ftp更新文件夹中文件个数
    private Integer downloadFtpFileTotal;
    //ftp更新文件夹中文件总大小
    private Long downloadFtpFileSize;
    //从ftp中下载下来的文件总数
    private Integer downloadLocalFileTotal;
    //从ftp中下载下来的文件总大小
    private Long downloadLocalFileSize;
    //本地所缺失的更新文件路径
    private List<String> missDownloadFilePath;
    //上传状态
    private Integer uploadStatus;
    //目标ftp更新文件夹中文件个数
    private Integer uploadFtpFileTotal;
    //目标ftp更新文件夹中文件总大小
    private Long uploadFtpFileSize;
    //本地更新文件总数
    private Integer uploadLocalFileTotal;
    //本地更新文件总大小
    private Long uploadLocalFileSize;
    //ftp中所缺失的更新文件路径
    private List<String> missUploadFilePath;
    public void init(String downName, Integer downloadStatus, Integer uploadStatus) {
        this.downName = downName;
        this.downloadStatus = downloadStatus;
        this.downloadFtpFileTotal = 0;
        this.downloadFtpFileSize = 0L;
        this.downloadLocalFileTotal = 0;
        this.downloadLocalFileSize = 0L;
        this.missDownloadFilePath = new ArrayList<>();
        this.uploadStatus = uploadStatus;
        this.uploadFtpFileTotal = 0;
        this.uploadFtpFileSize = 0L;
        this.uploadLocalFileTotal = 0;
        this.uploadLocalFileSize = 0L;
        this.missUploadFilePath = new ArrayList<>();
    }

    public String getDownName() {
        return downName;
    }

    public void setDownName(String downName) {
        this.downName = downName;
    }

    public Integer getDownloadStatus() {
        return downloadStatus;
    }

    public void setDownloadStatus(Integer downloadStatus) {
        this.downloadStatus = downloadStatus;
    }

    public Integer getDownloadFtpFileTotal() {
        return downloadFtpFileTotal;
    }

    public void setDownloadFtpFileTotal(Integer downloadFtpFileTotal) {
        this.downloadFtpFileTotal = downloadFtpFileTotal;
    }

    public Long getDownloadFtpFileSize() {
        return downloadFtpFileSize;
    }

    public void setDownloadFtpFileSize(Long downloadFtpFileSize) {
        this.downloadFtpFileSize = downloadFtpFileSize;
    }

    public Integer getDownloadLocalFileTotal() {
        return downloadLocalFileTotal;
    }

    public void setDownloadLocalFileTotal(Integer downloadLocalFileTotal) {
        this.downloadLocalFileTotal = downloadLocalFileTotal;
    }

    public Long getDownloadLocalFileSize() {
        return downloadLocalFileSize;
    }

    public void setDownloadLocalFileSize(Long downloadLocalFileSize) {
        this.downloadLocalFileSize = downloadLocalFileSize;
    }

    public List<String> getMissDownloadFilePath() {
        return missDownloadFilePath;
    }

    public void setMissDownloadFilePath(List<String> missDownloadFilePath) {
        this.missDownloadFilePath = missDownloadFilePath;
    }

    public Integer getUploadStatus() {
        return uploadStatus;
    }

    public void setUploadStatus(Integer uploadStatus) {
        this.uploadStatus = uploadStatus;
    }

    public Integer getUploadFtpFileTotal() {
        return uploadFtpFileTotal;
    }

    public void setUploadFtpFileTotal(Integer uploadFtpFileTotal) {
        this.uploadFtpFileTotal = uploadFtpFileTotal;
    }

    public Long getUploadFtpFileSize() {
        return uploadFtpFileSize;
    }

    public void setUploadFtpFileSize(Long uploadFtpFileSize) {
        this.uploadFtpFileSize = uploadFtpFileSize;
    }

    public Integer getUploadLocalFileTotal() {
        return uploadLocalFileTotal;
    }

    public void setUploadLocalFileTotal(Integer uploadLocalFileTotal) {
        this.uploadLocalFileTotal = uploadLocalFileTotal;
    }

    public Long getUploadLocalFileSize() {
        return uploadLocalFileSize;
    }

    public void setUploadLocalFileSize(Long uploadLocalFileSize) {
        this.uploadLocalFileSize = uploadLocalFileSize;
    }

    public List<String> getMissUploadFilePath() {
        return missUploadFilePath;
    }

    public void setMissUploadFilePath(List<String> missUploadFilePath) {
        this.missUploadFilePath = missUploadFilePath;
    }
}
