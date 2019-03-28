package com.wangyb.ftpdemo.config;

/**
 * Created with Intellij IDEA.
 *
 * @author wangyb
 * @Date 2019/3/27 15:19
 * Modified By:
 * Description:用于临时存储下载相关的配置信息，方便使用
 * 单例模式实现方式之一，避免多线程同步问题，自动支持序列化机制，防止反序列化重新创建新的对象
 */
public enum DownloadCommon {

    DOWNLOAD_COMMON;

    //文件存储ftp的主机地址
    private String ftpHost = "192.168.21.29";
    //文件存储ftp的端口
    private int ftpPort = 21;
    //ftp登录名
    private String ftpUserName = "root";
    //ftp登录密码
    private String ftpPassword = "root";
    //ftp中存储有更新内容的根地址（注意：去除ftp的工作目录，如：ftp的工作目录是：F:/ftpServer,而存有更新信息的路径是：F:/ftpServer/data/,则此处是：dada/）
    private String ftpPath = "/upload/";
    //将要存储的目的地址
    private String destinationPath = "D:/testData/";
    //邮件称谓
    private String emailName = "汪先生";
    //用于发送邮件的邮箱
    private String fromEmail;
    //用于接收邮件的邮箱
    private String[] receivers = new String[]{"1426924646@qq.com"};
    //下载哪一天的数据，0—当天的，-1-昨天的，-2-前天的，以此类推,如果为正整数则不自动添加下载任务
    private Integer downDate = 0;
    //是否监听发送邮件，默认发送邮件
    private Integer ifListen = 1;

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

    public String getDestinationPath() {
        return destinationPath;
    }

    public void setDestinationPath(String destinationPath) {
        this.destinationPath = destinationPath;
    }

    public String getEmailName() {
        return emailName;
    }

    public void setEmailName(String emailName) {
        this.emailName = emailName;
    }

    public String getFromEmail() {
        return fromEmail;
    }

    public void setFromEmail(String fromEmail) {
        this.fromEmail = fromEmail;
    }

    public String[] getReceivers() {
        return receivers;
    }

    public void setReceivers(String[] receivers) {
        this.receivers = receivers;
    }

    public Integer getDownDate() {
        return downDate;
    }

    public void setDownDate(Integer downDate) {
        this.downDate = downDate;
    }

    public Integer getIfListen() {
        return ifListen;
    }

    public void setIfListen(Integer ifListen) {
        this.ifListen = ifListen;
    }
}
