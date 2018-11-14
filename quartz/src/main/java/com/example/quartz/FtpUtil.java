package com.example.quartz;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
public class FtpUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(FtpUtil.class);

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
    public static FTPClient connectFtpServer(String host, int port, String userName, String password) throws Exception {
        FTPClient ftpClient = new FTPClient();
        try {
            LOGGER.info("开始连接ftp服务器");
            LOGGER.info("host:{}", host);
            LOGGER.info("port:{}", port);
            LOGGER.info("userName:{}", userName);
            ftpClient.connect(host, port);
            if (FTPReply.isPositiveCompletion(ftpClient.getReplyCode())) {
                if (ftpClient.login(userName, password)) {
                    LOGGER.info("登录ftp服务器成功");
                    // 开启服务器对UTF-8的支持，如果服务器支持就用UTF-8编码，否则就使用本地编码（GBK）
                    if (FTPReply.isPositiveCompletion(ftpClient.sendCommand("OPTS UTF8", "ON"))) {
                        LOCAL_CHARSET = "UTF-8";
                    }
                    ftpClient.setControlEncoding(LOCAL_CHARSET);
                    // 设置被动模式
                    ftpClient.enterLocalPassiveMode();
                    // 设置传输的模式
                    ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
                    return ftpClient;
                }
            }
        } catch (IOException e) {
            throw new Exception("连接ftp服务器失败");
        }
        return null;
    }

    /**
     * 获取已经登录并且工作文件夹已经在指定路径的ftp中文件的名字列表，不包括其中文件夹名字
     *
     * @param ftpClient
     * @return
     * @throws Exception
     */
    public static List<String> listImageName(FTPClient ftpClient) throws Exception {
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
    public static Boolean deleteImage(FTPClient ftpClient) throws Exception {
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

    private FtpUtil() {
        throw new IllegalStateException("Util Class");
    }
}
