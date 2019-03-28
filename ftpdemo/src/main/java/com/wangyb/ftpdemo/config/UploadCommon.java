package com.wangyb.ftpdemo.config;

/**
 * Created with Intellij IDEA.
 *
 * @author wangyb
 * @Date 2019/2/15 16:08
 * Modified By:
 * Description: 临时“存储”上传文件相关配置，方便使用
 * 单例模式实现方式之一，避免多线程同步问题，自动支持序列化机制，防止反序列化重新创建新的对象
 */
public enum UploadCommon {
    UPLOAD_COMMON;
    //存储的ftp
    private String ftpHost = "192.168.21.29";
    //ftp的端口
    private int ftpPort = 21;
    //ftp登录名
    private String ftpUserName = "root";
    //ftp登录密码
    private String ftpPassword = "root";
    //文件夹存放的目录
    private String ftpPath = "/testUpload/";
    //下载完毕后是否自动上传，默认自动上传
    private Integer ifUpload = 1;

    public String getFtpHost() {
        return ftpHost;
    }

    public void setFtpHost(String ftpHost) {
        this.ftpHost = ftpHost;
    }

    public int getFtpPort() {
        return ftpPort;
    }

    public void setFtpPort(int ftpPort) {
        this.ftpPort = ftpPort;
    }

    public String getFtpUserName() {
        return ftpUserName;
    }

    public void setFtpUserName(String ftpUserName) {
        this.ftpUserName = ftpUserName;
    }

    public String getFtpPassword() {
        return ftpPassword;
    }

    public void setFtpPassword(String ftpPassword) {
        this.ftpPassword = ftpPassword;
    }

    public String getFtpPath() {
        return ftpPath;
    }

    public void setFtpPath(String ftpPath) {
        this.ftpPath = ftpPath;
    }

    public Integer getIfUpload() {
        return ifUpload;
    }

    public void setIfUpload(Integer ifUpload) {
        this.ifUpload = ifUpload;
    }
}
