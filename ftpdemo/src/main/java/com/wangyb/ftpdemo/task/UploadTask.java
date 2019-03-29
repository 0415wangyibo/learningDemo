package com.wangyb.ftpdemo.task;

import com.wangyb.ftpdemo.config.DownloadCommon;
import com.wangyb.ftpdemo.config.UploadCommon;
import com.wangyb.ftpdemo.pojo.DayDownLoadInfo;
import com.wangyb.ftpdemo.pojo.JobCommon;
import com.wangyb.ftpdemo.service.DownLoadCommonService;
import com.wangyb.ftpdemo.service.SendMailService;
import com.wangyb.ftpdemo.service.UploadFtpService;
import com.wangyb.ftpdemo.service.UploadService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.List;

/**
 * Created with Intellij IDEA.
 *
 * @author wangyb
 * @Date 2019/2/15 15:38
 * Modified By:
 * Description: 上传定时任务
 */
@Component
@Slf4j
public class UploadTask {

    private final UploadService uploadService;

    private final SendMailService sendMailService;

    private final UploadFtpService uploadFtpService;

    private final DownLoadCommonService downLoadCommonService;

    private String uploadName = "";

    private Long uploadNowSize = -1L;

    @Autowired
    public UploadTask(UploadService uploadService, SendMailService sendMailService,
                      UploadFtpService uploadFtpService, DownLoadCommonService downLoadCommonService) {
        this.uploadService = uploadService;
        this.sendMailService = sendMailService;
        this.uploadFtpService = uploadFtpService;
        this.downLoadCommonService = downLoadCommonService;
    }

    /**
     * 每隔3分钟查看有没有下载好，但没有上传的文件夹,或者不存在的文件
     */
    @Scheduled(cron = "0 0/3 * * * ?")
    public void uploadTask() {
        log.debug("开始检查是否有下载完毕，但没有上传的文件");
        //如果没有设置上传则不上传
        if (UploadCommon.UPLOAD_COMMON.getIfUpload() == 0) {
            return;
        }
        //如果有正在下载、下载核查、正在上传或者正在核查上传文件是否完整的任务，则停止添加上传任务
        if (JobCommon.JOB_COMMON.getDayDownLoadInfoByStatus(1).size() != 0 || JobCommon.JOB_COMMON.getDayDownLoadInfoByStatus(2).size() != 0 ||
                JobCommon.JOB_COMMON.getListByStatusAndUploadStatus(3, 1).size() != 0 ||
                JobCommon.JOB_COMMON.getListByStatusAndUploadStatus(3, 2).size() != 0) {
            return;
        }
        List<DayDownLoadInfo> downLoadInfoList = JobCommon.JOB_COMMON.getListByStatusAndUploadStatus(3, 0);
        downLoadInfoList.addAll(JobCommon.JOB_COMMON.getListByStatusAndUploadStatus(3, -1));
        //本地文件不存在的也会进行查看，如果存在则会上传，不过优先级较低,需要等待已经存在的文件夹上传完毕
        downLoadInfoList.addAll(JobCommon.JOB_COMMON.getListByStatusAndUploadStatus(3, -3));
        if (downLoadInfoList.size() != 0) {
            log.info("开始上传文件夹：" + downLoadInfoList.get(0).getDownName());
            uploadService.uploadFileToFtp(downLoadInfoList.get(0));
        }
    }

    /**
     * 检查是否有上传完成的任务，如果有则发送邮件进行通知
     */
    @Scheduled(cron = "0 0/2 * * * ?")
    public void sendUploadTask() {
        log.debug("开始检查是否有上传成功的文件");
        List<DayDownLoadInfo> downLoadInfoList = JobCommon.JOB_COMMON.getListByStatusAndUploadStatus(3, 3);
        if (null != downLoadInfoList && downLoadInfoList.size() > 0) {
            for (DayDownLoadInfo dayDownLoadInfo : downLoadInfoList) {
                if (dayDownLoadInfo.getSendTimes() < 3) {
                    dayDownLoadInfo.setSendTimes(3);
                    JobCommon.JOB_COMMON.addDayDownLoadInfo(dayDownLoadInfo);
                    downLoadCommonService.saveDownLoadInfoToFile();
                    //如果不监听则不发送邮件
                    if (DownloadCommon.DOWNLOAD_COMMON.getIfListen() == 0) {
                        break;
                    }
                    if (UploadCommon.UPLOAD_COMMON.getIfUpload() != 0) {
                        String subject = "自动下载ftp文件夹任务通知";
                        String text = "尊敬的" + DownloadCommon.DOWNLOAD_COMMON.getEmailName() + "\n    文件夹：" + dayDownLoadInfo.getDownName() + "已经成功上传到ftp中。";
                        log.info("该文件夹已经成功上传到ftp中:" + dayDownLoadInfo.getDownName());
                        try {
                            sendMailService.sendTextMail(subject, text, DownloadCommon.DOWNLOAD_COMMON.getFromEmail(), DownloadCommon.DOWNLOAD_COMMON.getReceivers());
                        } catch (Exception e) {
                            log.error("上传成功邮件发送失败：" + dayDownLoadInfo.getDownName());
                        }
                    }
                }
            }
        }
    }

    /**
     * 每隔10分钟检查一次正在上传的文件，防止出现上传完一个文件后没有开始上传下一个文件的情况
     * 进一步解决ftp上传假死问题
     */
    @Scheduled(cron = "0 0/10 * * * ?")
    public void checkDownloadTask() {
        log.debug("开始检查是否有上传任务被阻塞");
        List<DayDownLoadInfo> nowUploadList = JobCommon.JOB_COMMON.getListByStatusAndUploadStatus(3, 1);
        if (nowUploadList.size() > 0) {
            DayDownLoadInfo dayDownLoadInfo = nowUploadList.get(0);
            log.debug("10分钟前正在上传：" + uploadName);
            log.debug("10分钟前文件大小：" + uploadNowSize);
            //如果正在上传文件，则判断是否上传完毕
            if (!StringUtils.isEmpty(dayDownLoadInfo.getUploadNow())) {
                String fullPath = dayDownLoadInfo.getUploadNow();
                String ftpHost = "";
                try {
                    FTPClient ftpClient;
                    ftpClient = uploadFtpService.open(UploadCommon.UPLOAD_COMMON.getFtpHost(), UploadCommon.UPLOAD_COMMON.getFtpPort(),
                            UploadCommon.UPLOAD_COMMON.getFtpUserName(), UploadCommon.UPLOAD_COMMON.getFtpPassword());
                    ftpHost = UploadCommon.UPLOAD_COMMON.getFtpHost();
                    String path = fullPath.substring(0, fullPath.lastIndexOf("/"));
                    String fileName = org.apache.commons.lang.StringUtils.substring(fullPath, fullPath.lastIndexOf("/") + 1);
                    ftpClient.changeWorkingDirectory(path);
                    FTPFile[] ftpFiles = ftpClient.listFiles();
                    Integer flag = 0;
                    for (FTPFile ftpFile : ftpFiles) {
                        if (ftpFile.getName().equals(fileName)) {
                            flag = 1;
                            //如果正在上传的文件和10分钟前的一样，大小也一样则说明上传任务被阻塞
                            if ((!uploadName.isEmpty()) &&fileName.equals(uploadName) && ftpFile.getSize() == uploadNowSize) {
                                log.error("由于文件：" + fullPath + "阻塞进程，程序强行关闭ftp，重新上传");
                                dayDownLoadInfo.setUploadNow(null);
                                dayDownLoadInfo.setUploadNowSize(null);
                                dayDownLoadInfo.setUploadStatus(0);
                                dayDownLoadInfo.setUploadReason("由于文件：" + fullPath + "阻塞进程，程序强行关闭ftp，重新上传");
                                JobCommon.JOB_COMMON.addDayDownLoadInfo(dayDownLoadInfo);
                                uploadName = "";
                                uploadNowSize = -1L;
                                log.info(dayDownLoadInfo.getUploadReason() + "，任务名：" + dayDownLoadInfo.getDownName());
                                uploadFtpService.close();
                            } else {
                                //记录正在上传的文件名及已经上传的大小
                                uploadName = fileName;
                                uploadNowSize = ftpFile.getSize();
                            }
                            break;
                        }
                    }
                    //如果正在上传的文件ftp不存在，则说明上传出错，重新上传
                    if (flag == 0) {
                        log.error("由于文件：" + fullPath + "阻塞进程，程序强行关闭ftp，重新上传");
                        uploadName = "";
                        uploadNowSize = -1L;
                        dayDownLoadInfo.setUploadStatus(0);
                        dayDownLoadInfo.setUploadReason("由于文件：" + fullPath + "阻塞进程，程序强行关闭ftp，重新上传");
                        dayDownLoadInfo.setUploadNow(null);
                        dayDownLoadInfo.setUploadNowSize(null);
                        JobCommon.JOB_COMMON.addDayDownLoadInfo(dayDownLoadInfo);
                        log.info(dayDownLoadInfo.getUploadReason() + "，任务名：" + dayDownLoadInfo.getDownName());
                        uploadFtpService.close();
                    }
                } catch (IOException e) {
                    log.error("关闭上传ftp服务器失败，可能连接已经中断:" + ftpHost + "任务名：" + dayDownLoadInfo.getDownName());
                }
            }
        }
    }
}
