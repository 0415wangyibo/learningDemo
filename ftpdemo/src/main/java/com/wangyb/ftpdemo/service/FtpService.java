package com.wangyb.ftpdemo.service;

import com.wangyb.ftpdemo.config.DownloadCommon;
import com.wangyb.ftpdemo.config.StatisticsCommon;
import com.wangyb.ftpdemo.pojo.DayDownLoadInfo;
import com.wangyb.ftpdemo.pojo.JobCommon;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with Intellij IDEA.
 *
 * @author wangyb
 * @Date 2019/1/29 10:39
 * Modified By:
 * Description: 下载ftp服务
 */
@Service
@Slf4j
public class FtpService {

    /**
     * 定义线程私有成员变量，防止多个线程同时修改私有成员变量时出现异常
     */
    private ThreadLocal<FTPClient> ftpClientThreadLocal = new ThreadLocal<>();

    /**
     * 连接 ftp 服务器，登录，设置被动模式
     *
     * @return
     * @throws IOException
     */
    public FTPClient open() throws IOException {
        log.debug("开始连接登录ftp服务器");
        FTPClient ftpClient = new FTPClient();
        ftpClient.connect(DownloadCommon.DOWNLOAD_COMMON.getFtpHost(), DownloadCommon.DOWNLOAD_COMMON.getFtpPort());
        //打印命令
//        ftpClient.addProtocolCommandListener(new PrintCommandListener(System.out));
        int reply = ftpClient.getReplyCode();
        if (!FTPReply.isPositiveCompletion(reply)) {
            ftpClient.disconnect();
            throw new IOException("连接服务器异常");
        }
        if (!ftpClient.login(DownloadCommon.DOWNLOAD_COMMON.getFtpUserName(), DownloadCommon.DOWNLOAD_COMMON.getFtpPassword())) {
            ftpClient.disconnect();
            throw new IOException("登录服务器异常");
        }
        ftpClient.setBufferSize(32 * 1024 * 1024);
        ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
        //设置为被动模式
        ftpClient.enterLocalPassiveMode();
        //每隔半分钟发送一次心跳，向路由器表明网络正在使用，防止路由器切断网络
        ftpClient.setControlKeepAliveTimeout(30);
        ftpClientThreadLocal.set(ftpClient);
        return ftpClient;
    }

    /**
     * 下载文件
     *
     * @param source          ftp文件名
     * @param destination     本地文件绝对路径
     * @param fileSize        文件大小
     * @param sourcePath      ftp工作路径
     * @param dayDownLoadInfo 下载信息
     * @param sameDownload    同名通大小文件是否下载
     * @throws IOException
     */
    public void downloadFile(String source, String destination, Long fileSize, String sourcePath, DayDownLoadInfo dayDownLoadInfo, Integer sameDownload) throws IOException {
        File file = new File(destination);
        //如果文件已经存在而且大小相同则跳过不下载
        if (file.exists()) {
            if (sameDownload == 0) {
                if (file.length() == fileSize) {
                    return;
                }
            } else {
                file.delete();
            }
        }
        log.debug("正在下载：" + sourcePath + "/" + source);
        dayDownLoadInfo.setDownloadNow(destination);
        dayDownLoadInfo.setDownloadNowSize(fileSize);
        JobCommon.JOB_COMMON.addDayDownLoadInfo(dayDownLoadInfo);
        FTPClient ftpClient = ftpClientThreadLocal.get();
        ftpClient.changeWorkingDirectory(sourcePath);
        //告诉ftp服务器开通一个端口传输数据
        ftpClient.enterLocalPassiveMode();
        try (FileOutputStream out = new FileOutputStream(destination)) {
            //有时候程序会卡死在这里，该文件可以正常下载完成，但是不会接着往下执行，需要等待定时任务强行关闭与ftp的连接，更改任务状态以实现继续下载
            ftpClient.retrieveFile(source, out);
            out.flush();
            out.close();
            //如果下载的大小超过2M则重新连接下载，防止文件过大，下载之后不再继续下载文件
            if (fileSize > 1024 * 1204 * 2) {
                this.close();
                this.open();
            }
        }
    }

    /**
     * 下载 ftp 上某个文件夹到本地
     *
     * @param sourcePath      ftp 文件夹名称，必须是登录之后默认路径下的文件夹
     * @param destinationPath 本地文件夹路径，后面要加 /
     * @param dayDownLoadInfo 下载信息
     * @param sameDownload    文件大小相同是否重新下载
     * @throws IOException
     */
    public void downloadDirectory(String sourcePath, String destinationPath, DayDownLoadInfo dayDownLoadInfo, Integer sameDownload) throws IOException, NullPointerException {
        FTPClient ftpClient = ftpClientThreadLocal.get();
        ftpClient.changeWorkingDirectory(sourcePath);
        FTPFile[] ftpFiles = ftpClient.listFiles();
        if (null == ftpFiles || ftpFiles.length < 1) {
            return;
        }
        for (FTPFile f : ftpFiles) {
            if (f.isDirectory()) {
                File file = new File(destinationPath + f.getName() + "/");
                file.mkdir();
                downloadDirectory(sourcePath + "/" + f.getName(), destinationPath + f.getName() + "/", dayDownLoadInfo, sameDownload);
            } else {
                //如果文件为发布中，则不下载
                if (f.getName().contains(".tmp")) {
                    continue;
                }
                //只下载大于1KB的文件
                if (f.getSize() > 1024) {
                    downloadFile(f.getName(), destinationPath + f.getName(), f.getSize(), sourcePath, dayDownLoadInfo, sameDownload);
                    //防止多线程同时下载使得其中一个线程异常，避免重复添加下载任务
                    dayDownLoadInfo.setStatus(1);
                    dayDownLoadInfo.setDownloadNow(null);
                    dayDownLoadInfo.setDownloadNowSize(null);
                    dayDownLoadInfo.setLoadedTotal(dayDownLoadInfo.getLoadedTotal() + f.getSize());
                    dayDownLoadInfo.setLoadedFileNumber(dayDownLoadInfo.getLoadedFileNumber() + 1);
                    JobCommon.JOB_COMMON.addDayDownLoadInfo(dayDownLoadInfo);
                } else {
                    log.info("小于1KB的异常文件未下载：" + sourcePath + "/" + f.getName());
                }
            }
        }
    }

    /**
     * 删除 ftp 上某个文件夹下的内容，但保留该文件夹
     *
     * @param pathname
     * @throws IOException
     */
    public void deleteAllFile(String pathname) throws IOException {
        FTPClient ftpClient = ftpClientThreadLocal.get();
        ftpClient.changeWorkingDirectory(pathname);
        FTPFile[] files = ftpClient.listFiles();
        for (FTPFile f : files) {
            if (f.isDirectory()) {
                deleteAllFile(f.getName());
                ftpClient.removeDirectory(f.getName());
            } else {
                ftpClient.deleteFile(f.getName());
            }
        }
        ftpClient.changeToParentDirectory();
    }

    public List<String> listFiles() throws IOException {
        return listFiles(null);
    }

    /**
     * 列出指定路径下文件名，可用于判断是否有需要下载的文件
     *
     * @param pathname ftp中文件夹路径
     * @return
     * @throws IOException
     */
    public List<String> listFiles(String pathname) throws IOException {
        FTPClient ftpClient = ftpClientThreadLocal.get();
        FTPFile[] files = ftpClient.listFiles(pathname);
        List<String> list = new ArrayList<>();
        for (FTPFile f : files) {
            list.add(f.getName());
        }
        return list;
    }

    /**
     * 警告：调用此方法断开连接，正在下载的文件夹也会停止下载
     * 只有当下载处于假死状态，阻碍下载进程时才会调用
     * 退出登录，断开连接
     *
     * @throws IOException
     */
    public void close() throws IOException {
        FTPClient ftpClient = ftpClientThreadLocal.get();
        if (ftpClient != null) {
            log.info("ftp主机：" + ftpClient.getPassiveHost());
            //终止传输
            ftpClient.abort();
            if (ftpClient.isConnected()) {
                if (ftpClient.logout()) {
                    log.info("登出成功");
                }
                ftpClient.disconnect();
            }
        }
    }

    /**
     * 判断是否连接
     *
     * @return
     * @throws IOException
     */
    public Boolean isConnected() throws IOException {
        FTPClient ftpClient = ftpClientThreadLocal.get();
        Boolean connected = false;
        if (ftpClient != null) {
            connected = ftpClient.sendNoOp();
        }
        log.debug("连接ftp服务器是否成功： " + connected);
        return connected;
    }

    /**
     * 判断是否可以切换工作路径到指定位置，如果可以则将工作路径切换回根路径，并返回true
     *
     * @param pathname
     * @return
     * @throws IOException
     */
    public Boolean testChangeWorkingDirectory(String pathname) throws IOException {
        FTPClient ftpClient = ftpClientThreadLocal.get();
        if (ftpClient.changeWorkingDirectory(pathname)) {
            ftpClient.changeWorkingDirectory("/");
            return true;
        }
        return false;
    }

    /**
     * 获得指定ftp路径中文件的总大小
     *
     * @param pathname ftp中文件夹路径，最后没有'/'
     * @return
     * @throws IOException
     */
    public Long getFileSize(String pathname) throws IOException {
        FTPClient ftpClient = ftpClientThreadLocal.get();
        ftpClient.changeWorkingDirectory(pathname);
        Long size = 0L;
        FTPFile[] files = ftpClient.listFiles(pathname);
        for (FTPFile ftpFile : files) {
            if (ftpFile.isDirectory()) {
                size = size + getFileSize(pathname + "/" + ftpFile.getName());
            } else {
                //.tmp文件为正在发布的文件，不统计
                if (!ftpFile.getName().contains(".tmp")) {
                    //只统计大小大于1KB的文件，小于1KB的为错误文件
                    if (ftpFile.getSize() > 1024) {
                        size = size + ftpFile.getSize();
                    }
                }
            }
        }
        return size;
    }

    /**
     * 获得ftp中指定路径中所有文件的个数
     *
     * @param pathname ftp中文件夹路径，最后没有'/'
     * @return
     * @throws IOException
     */
    public Integer getFileNumber(String pathname) throws IOException {
        FTPClient ftpClient = ftpClientThreadLocal.get();
        ftpClient.changeWorkingDirectory(pathname);
        Integer size = 0;
        FTPFile[] files = ftpClient.listFiles(pathname);
        for (FTPFile ftpFile : files) {
            if (ftpFile.isDirectory()) {
                size = size + getFileNumber(pathname + "/" + ftpFile.getName());
            } else {
                //.tmp文件为正在发布的文件，不统计
                if (!ftpFile.getName().contains(".tmp")) {
                    //只统计大于1KB的文件
                    if (ftpFile.getSize() > 1024) {
                        size = size + 1;
                    }
                }
            }
        }
        return size;
    }

    /**
     * 核查ftp中文件夹内容是否和本地文件夹是否相同，如果本地文件缺失，则会统计缺失文件的路径
     *
     * @param sourcePath      ftp中文件夹路径
     * @param destinationPath 本地文件夹路径
     * @param dayDownLoadInfo 用于存储核查结果
     * @throws IOException
     */
    public void checkDownLoadFile(String sourcePath, String destinationPath, DayDownLoadInfo dayDownLoadInfo) throws IOException {
        FTPClient ftpClient = ftpClientThreadLocal.get();
        ftpClient.changeWorkingDirectory(sourcePath);
        FTPFile[] files = ftpClient.listFiles(sourcePath);
        for (FTPFile ftpFile : files) {
            if (ftpFile.isDirectory()) {
                File file = new File(destinationPath + ftpFile.getName() + "/");
                if (!file.exists()) {
                    file.mkdir();
                }
                checkDownLoadFile(sourcePath + "/" + ftpFile.getName(), destinationPath + ftpFile.getName() + "/", dayDownLoadInfo);
            } else {
                //如果文件是正在发布中，则跳过
                if (ftpFile.getName().contains(".tmp")) {
                    continue;
                }
                //如果文件小于1KB则不统计
                if (ftpFile.getSize() <= 1024) {
                    continue;
                }
                File file = new File(destinationPath + ftpFile.getName());
                //如果文件不存在或者文件大小和ftp上的不同，则说明文件下载不完整，将缺失的路径进行存储
                if (!file.exists() || file.length() != ftpFile.getSize()) {
                    List<String> missPath = dayDownLoadInfo.getMissPath();
                    missPath.add(destinationPath + ftpFile.getName());
                    dayDownLoadInfo.setMissPath(missPath);
                    JobCommon.JOB_COMMON.addDayDownLoadInfo(dayDownLoadInfo);
                }
            }
        }
    }

    /**
     * 统计信息，并核查ftp中adi文件夹内容是否和本地文件夹是否相同，如果本地文件缺失，则会统计缺失文件的路径
     *
     * @param sourcePath      ftp中文件夹路径
     * @param destinationPath 本地文件夹路径
     * @throws IOException
     */
    public void statisticDownloadFile(String sourcePath, String destinationPath) throws IOException {
        FTPClient ftpClient = ftpClientThreadLocal.get();
        ftpClient.changeWorkingDirectory(sourcePath);
        FTPFile[] files = ftpClient.listFiles(sourcePath);
        for (FTPFile ftpFile : files) {
            if (ftpFile.isDirectory()) {
                File file = new File(destinationPath + ftpFile.getName() + "/");
                if (!file.exists()) {
                    file.mkdir();
                }
                statisticDownloadFile(sourcePath + "/" + ftpFile.getName(), destinationPath + ftpFile.getName() + "/");
            } else {
                //如果文件是正在发布中，则跳过
                if (ftpFile.getName().contains(".tmp")) {
                    continue;
                }
                //如果文件小于1KB则不统计
                if (ftpFile.getSize() <= 1024) {
                    continue;
                }
                //统计个数
                StatisticsCommon.STATISTICS_COMMON.setDownloadFtpFileTotal(StatisticsCommon.STATISTICS_COMMON.getDownloadFtpFileTotal() + 1);
                StatisticsCommon.STATISTICS_COMMON.setDownloadFtpFileSize(StatisticsCommon.STATISTICS_COMMON.getDownloadFtpFileSize() + ftpFile.getSize());
                File file = new File(destinationPath + ftpFile.getName());
                //如果文件不存在或者文件大小和ftp上的不同，则说明文件下载不完整，将缺失的路径进行存储
                if (!file.exists() || file.length() != ftpFile.getSize()) {
                    List<String> missPath = StatisticsCommon.STATISTICS_COMMON.getMissDownloadFilePath();
                    missPath.add(ftpFile.getName());
                    StatisticsCommon.STATISTICS_COMMON.setMissDownloadFilePath(missPath);
                } else {
                    StatisticsCommon.STATISTICS_COMMON.setDownloadLocalFileTotal(StatisticsCommon.STATISTICS_COMMON.getDownloadLocalFileTotal() + 1);
                    StatisticsCommon.STATISTICS_COMMON.setDownloadLocalFileSize(StatisticsCommon.STATISTICS_COMMON.getDownloadLocalFileSize() + ftpFile.getSize());
                }
            }
        }
    }
}
