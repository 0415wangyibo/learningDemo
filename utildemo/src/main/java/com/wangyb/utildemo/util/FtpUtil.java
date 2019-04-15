package com.wangyb.utildemo.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * Created with Intellij IDEA.
 *
 * @author wangyb
 * @Date 2018/7/12 14:52
 * Modified By:
 * Description:ftp相关工具类
 */
@UtilityClass
@Slf4j
public class FtpUtil {

    //本地字符编码
    private static String LOCAL_CHARSET = "GBK";

    /**
     * 登录指定的服务器，并调整编码格式
     *
     * @param host
     * @param port
     * @param userName
     * @param password
     * @return
     * @throws Exception
     */
    public FTPClient connectFtpServer(String host, int port, String userName, String password) throws Exception {
        FTPClient ftpClient = new FTPClient();
        try {
            log.info("开始连接ftp服务器");
            log.info("host:{}", host);
            log.info("port:{}", port);
            log.info("userName:{}", userName);
            //设置连接超时时间为10秒
            ftpClient.setConnectTimeout(10 * 1000);
            ftpClient.connect(host, port);
            //打印命令
//        ftpClient.addProtocolCommandListener(new PrintCommandListener(System.out));
            int reply = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftpClient.disconnect();
                throw new IOException("连接服务器异常");
            }
            if (!ftpClient.login(userName, password)) {
                ftpClient.disconnect();
                throw new IOException("登录服务器异常");
            }
            //设置缓冲区大小可以提高下载上传速度
            ftpClient.setBufferSize(32 * 1024 * 1024);
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
            //设置为被动模式
            ftpClient.enterLocalPassiveMode();
            //每隔半分钟发送一次心跳，向路由器表明网络正在使用，防止路由器切断网络
            ftpClient.setControlKeepAliveTimeout(30);
            log.debug("打开ftp操作结束");
            return ftpClient;
        } catch (IOException e) {
            throw new Exception("连接ftp服务器失败");
        }
    }

    /**
     * 获取已经登录并且工作文件夹已经在指定路径的ftp中文件的名字列表，不包括其中文件夹名字
     *
     * @param ftpClient
     * @return
     * @throws Exception
     */
    public List<String> listImageName(FTPClient ftpClient) throws Exception {
        try {
            // 获得ftp文件
            FTPFile[] ftpFiles = ftpClient.listFiles();
            List<String> imageNames = new LinkedList<>();
            if (null != ftpFiles && ftpFiles.length > 0) {
                for (FTPFile file : ftpFiles) {
                    // 只获取文件的名字
                    if (file.isFile()) {
                        imageNames.add(file.getName());
                    }
                }
            }
            return imageNames;
        } catch (IOException e) {
            throw new Exception("获取ftp服务器文件失败");
        }
    }

    /**
     * 删除已经登录并且工作文件夹已经在指定路径的ftp中的文件
     *
     * @param ftpClient
     * @return
     * @throws Exception
     */
    public Boolean deleteImage(FTPClient ftpClient) throws Exception {
        try {
            FTPFile[] ftpFiles = ftpClient.listFiles();
            if (null != ftpFiles && ftpFiles.length > 0) {
                for (FTPFile file : ftpFiles) {
                    //删除文件
                    if (file.isFile()) {
                        ftpClient.deleteFile(file.getName());
                    } else {
                        //删除文件夹
                        ftpClient.removeDirectory(file.getName());
                    }
                }
            }
            return true;
        } catch (IOException e) {
            throw new Exception("获取ftp中文件失败");
        }
    }

    /**
     * 删除指定尺寸的海报
     *
     * @param ftpClient
     * @param name      海报名字
     * @return 是否删除成功
     * @throws Exception
     */
    public Boolean deleteOneImage(FTPClient ftpClient, String name) throws Exception {
        try {
            FTPFile[] ftpFiles = ftpClient.listFiles();
            if (null != ftpFiles && ftpFiles.length > 0) {
                for (FTPFile file : ftpFiles) {
                    //删除文件
                    if (file.isFile()) {
                        String fileFullName = file.getName();
                        String fileName = fileFullName.substring(0, fileFullName.lastIndexOf("."));
                        if (fileName.equals(name)) {
                            ftpClient.deleteFile(fileFullName);
                        }
                    }
                }
            }
            return true;
        } catch (IOException e) {
            throw new Exception("获取ftp中文件失败");
        }
    }

    /**
     * 获得海报的ftp地址
     *
     * @param posterPath
     * @param seriesId
     * @param videoIndex
     * @return
     * @throws Exception
     */
    public String getPosterFtpPath(String posterPath, Integer seriesId, Integer videoIndex) throws Exception {
        if (null == videoIndex) {
            videoIndex = 0;
        }
        String filePath = posterPath + seriesId + "/" + videoIndex + "/";
        //防止中文乱码
        filePath = new String(filePath.getBytes("GBK"), "ISO-8859-1");
        return filePath;
    }
}
