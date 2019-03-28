package com.wangyb.ftpdemo.service;

import com.wangyb.ftpdemo.config.StatisticsCommon;
import com.wangyb.ftpdemo.pojo.DayDownLoadInfo;
import com.wangyb.ftpdemo.pojo.JobCommon;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Created with Intellij IDEA.
 *
 * @author wangyb
 * @Date 2019/2/15 17:21
 * Modified By:
 * Description: 上传ftp服务
 */
@Service
@Slf4j
public class UploadFtpService {

    private FTPClient ftpClient;

    /**
     * 连接 ftp 服务器，登录，设置被动模式
     *
     * @param host     ftp主机地址
     * @param port     端口
     * @param user     用户名
     * @param password 密码
     * @return
     * @throws IOException
     */
    public FTPClient open(String host, Integer port, String user, String password) throws IOException {
        log.debug("开始连接登录ftp服务器");
        ftpClient = new FTPClient();
        ftpClient.connect(host, port);
        //打印命令
//        ftpClient.addProtocolCommandListener(new PrintCommandListener(System.out));
        int reply = ftpClient.getReplyCode();
        if (!FTPReply.isPositiveCompletion(reply)) {
            ftpClient.disconnect();
            throw new IOException("连接服务器异常");
        }
        if (!ftpClient.login(user, password)) {
            ftpClient.disconnect();
            throw new IOException("登录服务器异常");
        }
        //上传时缓存设置太大，虽然可以提高上传速度，但电脑特别卡，故从32M降为4M
        ftpClient.setBufferSize(4 * 1024 * 1024);
        ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
        //设置为被动模式
        ftpClient.enterLocalPassiveMode();
        //每隔半分钟发送一次心跳，向路由器表明网络正在使用，防止路由器切断网络
        ftpClient.setControlKeepAliveTimeout(30);
        return ftpClient;
    }

    /**
     * 关闭ftp
     *
     * @throws IOException
     */
    public void close() throws IOException {
        if (ftpClient != null) {
            ftpClient.logout();
            ftpClient.disconnect();
        }
    }

    /**
     * 上传文件
     *
     * @param source      本地文件全路径
     * @param destination 目标文件名
     * @throws IOException
     */
    public void uploadFile(String source, String destination) throws IOException {
        ftpClient.enterLocalPassiveMode();
        try (InputStream is = new FileInputStream(source)) {
            ftpClient.storeFile(destination, is);
            is.close();
        }
    }

    /**
     * 将本地文件上传到ftp目录
     *
     * @param source          本地文件全路径
     * @param destinationPath ftp文件夹路径，含"/"
     * @param fileName        文件名
     * @throws IOException
     */
    public void uploadStatusFile(String source, String destinationPath, String fileName) throws IOException {
        changeAndMakeFtpDirectory(destinationPath);
        ftpClient.enterLocalPassiveMode();
        try (InputStream is = new FileInputStream(source)) {
            ftpClient.storeFile(fileName, is);
            is.close();
        }
    }

    /**
     * 上传本地某个文件夹到 ftp
     *
     * @param sourcePath      本地文件夹路径，后面要加 /
     * @param destinationPath ftp 上文件夹名称，必须是登录之后默认路径下的文件夹
     * @param dayDownLoadInfo 任务信息
     * @param sameUpload      相同文件名及大小的文件是否上传
     * @throws IOException
     */
    public void uploadDirectory(String sourcePath, String destinationPath, DayDownLoadInfo dayDownLoadInfo, Integer sameUpload) throws IOException,NullPointerException {
        changeAndMakeFtpDirectory(destinationPath);
        File[] files = new File(sourcePath).listFiles();
        log.debug(sourcePath);
        if (null == files || files.length < 1) {
            log.debug("本地文件夹为空");
            return;
        }
        for (File f : files) {
            if (f.isDirectory()) {
                uploadDirectory(f.getAbsolutePath(), destinationPath + f.getName() + "/", dayDownLoadInfo, sameUpload);
            } else {
                log.debug("本地文件名：" + f.getName());
                //如果ftp上已经存在同名同大小的文件则不上传
                Long fileSize = checkFileSize(f.getName());
                log.debug("ftp中文件大小:" + fileSize);
                log.debug("本地文件大小：" + f.length());
                //如果文件大小不同，或者文件要求重新上传，再重新上传
                if (fileSize != f.length() || sameUpload == 1) {
                    if (fileSize != 0L) {
                        ftpClient.deleteFile(f.getName());
                    }
                    //记录上传的文件信息
                    dayDownLoadInfo.setUploadNow(destinationPath + f.getName());
                    dayDownLoadInfo.setUploadNowSize(f.length());
                    JobCommon.JOB_COMMON.addDayDownLoadInfo(dayDownLoadInfo);
                    uploadFile(f.getAbsolutePath(), f.getName());
                    log.debug("该文件上传完毕：" + destinationPath + f.getName());
                    log.debug(destinationPath + f.getName());
                    //上传完毕则清空上传的文件信息
                    dayDownLoadInfo.setUploadNow(null);
                    dayDownLoadInfo.setUploadNowSize(null);
                }
                dayDownLoadInfo.setUploadStatus(1);
                dayDownLoadInfo.setUploadedTotal(dayDownLoadInfo.getUploadedTotal() + f.length());
                dayDownLoadInfo.setUploadedFileNumber(dayDownLoadInfo.getUploadedFileNumber() + 1);
                JobCommon.JOB_COMMON.addDayDownLoadInfo(dayDownLoadInfo);
            }
        }
    }

    /**
     * 创建并切换工作路径到指定位置,liux中ftp好像不支持按绝对路径创建文件夹
     *
     * @param destinationPath 目标位置
     * @throws IOException
     */
    private void changeAndMakeFtpDirectory(String destinationPath) throws IOException {
        String currentPath = ftpClient.printWorkingDirectory();
        String[] oldPaths = currentPath.split("/");
        if (oldPaths.length > 1) {
            for (int j = 1; j < oldPaths.length; j++) {
                ftpClient.changeToParentDirectory();
            }
        }
        String[] newPaths = destinationPath.split("/");
        if (newPaths.length > 1) {
            for (int i = 1; i < newPaths.length; i++) {
                if (!ftpClient.changeWorkingDirectory(newPaths[i])) {
                    ftpClient.makeDirectory(newPaths[i]);
                    ftpClient.changeWorkingDirectory(newPaths[i]);
                }
            }
        }
        log.debug("当前工作路径：" + ftpClient.printWorkingDirectory());
    }

    /**
     * 核查ftp中文件是否和本地文件相同，如果有丢失的文件，则进行统计记录
     *
     * @param sourcePath      本地文件路径
     * @param destinationPath ftp中文件路径
     * @param dayDownLoadInfo 统计核查结果
     * @throws IOException
     */
    public void checkDirectory(String sourcePath, String destinationPath, DayDownLoadInfo dayDownLoadInfo) throws IOException {
        changeAndMakeFtpDirectory(destinationPath);
        File[] files = new File(sourcePath).listFiles();
        if (null == files || files.length < 1) {
            return;
        }
        for (File f : files) {
            if (f.isDirectory()) {
                checkDirectory(f.getAbsolutePath(), destinationPath + f.getName() + "/", dayDownLoadInfo);
            } else {
                //如果ftp上已经存在同名同大小的文件则不统计
                if (checkFileSize(f.getName()) != f.length()) {
                    List<String> missPath = dayDownLoadInfo.getUploadMissPath();
                    missPath.add(destinationPath + f.getName());
                    dayDownLoadInfo.setUploadMissPath(missPath);
                    JobCommon.JOB_COMMON.addDayDownLoadInfo(dayDownLoadInfo);
                }
            }
        }
    }

    /**
     * 查看指定文件是否存在，如果存在返回文件大小，不存在则大小为0
     *
     * @param fileName 文件名字
     * @return
     */
    public Long checkFileSize(String fileName) throws IOException {
        FTPFile[] ftpFiles = ftpClient.listFiles();
        if (null != ftpFiles && ftpFiles.length > 0) {
            for (FTPFile ftpFile : ftpFiles) {
                if (ftpFile.getName().equals(fileName)) {
                    return ftpFile.getSize();
                }
            }
        }
        return 0L;
    }

    /**
     * 统计、核查ftp中文件是否和本地文件相同，如果有丢失的文件，则进行统计记录
     *
     * @param sourcePath      本地文件路径
     * @param destinationPath ftp中文件路径
     * @throws IOException
     */
    public void statisticsFile(String sourcePath, String destinationPath) throws IOException {
        changeAndMakeFtpDirectory(destinationPath);
        File[] files = new File(sourcePath).listFiles();
        if (null == files || files.length < 1) {
            return;
        }
        for (File f : files) {
            if (f.isDirectory()) {
                statisticsFile(f.getAbsolutePath(), destinationPath + f.getName() + "/");
            } else {
                //统计本地更新文件大小
                StatisticsCommon.STATISTICS_COMMON.setUploadLocalFileTotal(StatisticsCommon.STATISTICS_COMMON.getUploadLocalFileTotal() + 1);
                StatisticsCommon.STATISTICS_COMMON.setUploadLocalFileSize(StatisticsCommon.STATISTICS_COMMON.getUploadLocalFileSize() + f.length());
                //如果ftp上已经存在同名同大小的文件则说明该文件上传正常
                if (checkFileSize(f.getName()) != f.length()) {
                    List<String> missPath = StatisticsCommon.STATISTICS_COMMON.getMissUploadFilePath();
                    missPath.add(f.getName());
                    StatisticsCommon.STATISTICS_COMMON.setMissUploadFilePath(missPath);
                } else {
                    //统计上传成功的文件
                    StatisticsCommon.STATISTICS_COMMON.setUploadFtpFileTotal(StatisticsCommon.STATISTICS_COMMON.getUploadFtpFileTotal() + 1);
                    StatisticsCommon.STATISTICS_COMMON.setUploadFtpFileSize(StatisticsCommon.STATISTICS_COMMON.getUploadFtpFileSize() + f.length());
                }
            }
        }
    }
}
