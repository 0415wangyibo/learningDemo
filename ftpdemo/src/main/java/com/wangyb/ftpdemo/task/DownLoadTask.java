package com.wangyb.ftpdemo.task;

import com.wangyb.ftpdemo.config.DownloadCommon;
import com.wangyb.ftpdemo.pojo.DayDownLoadInfo;
import com.wangyb.ftpdemo.pojo.JobCommon;
import com.wangyb.ftpdemo.service.DownLoadCommonService;
import com.wangyb.ftpdemo.service.DownLoadService;
import com.wangyb.ftpdemo.service.FtpService;
import com.wangyb.ftpdemo.service.SendMailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

/**
 * Created with Intellij IDEA.
 *
 * @author wangyb
 * @Date 2019/1/30 16:05
 * Modified By: 下载定时任务
 * Description:
 */
@Component
@Slf4j
public class DownLoadTask {

    private final SendMailService sendMailService;

    private final DownLoadCommonService downLoadCommonService;

    private final FtpService ftpService;

    private final DownLoadService downLoadService;

    private Long downLoadNowSize = -1L;

    private String downLoadName = "";

    @Autowired
    public DownLoadTask(SendMailService sendMailService, DownLoadCommonService downLoadCommonService,
                        FtpService ftpService, DownLoadService downLoadService) {
        this.sendMailService = sendMailService;
        this.downLoadCommonService = downLoadCommonService;
        this.ftpService = ftpService;
        this.downLoadService = downLoadService;
    }

    /**
     * 每天10点定时添加下载任务到队列
     */
    @Scheduled(cron = "0 0 10 ? * *")
    public void downLoadTask() {
        Integer downDate = DownloadCommon.DOWNLOAD_COMMON.getDownDate();
        //根据downDate设定下载文件夹的任务
        if (downDate <= 0) {
            LocalDate localDate = LocalDate.now();
            localDate = localDate.plusDays(downDate);
            String downName = localDate.toString();
            downName = downName.replace("-", "");
            DayDownLoadInfo dayDownLoadInfo = JobCommon.JOB_COMMON.getDownNameSameInfo(downName);
            if (null != dayDownLoadInfo) {
                log.info("该任务已经存在:" + downName);
            } else {
                dayDownLoadInfo = new DayDownLoadInfo(downName, 0);
                JobCommon.JOB_COMMON.addDayDownLoadInfo(dayDownLoadInfo);
                downLoadCommonService.saveDownLoadInfoToFile();
                log.info("定时添加下载任务成功:" + downName);
            }
        }
    }


    /**
     * 每隔5分钟检查一次正在下载的文件，防止出现下载完一个文件后没有开始下载下一个文件的情况
     * 进一步解决ftp下载假死问题
     */
    @Scheduled(cron = "0 0/5 * * * ?")
    public void checkDownloadTask() {
        log.debug("开始检查是否有下载任务被阻塞");
        List<DayDownLoadInfo> nowDownloadList = JobCommon.JOB_COMMON.getDayDownLoadInfoByStatus(1);
        if (nowDownloadList.size() > 0) {
            DayDownLoadInfo dayDownLoadInfo = nowDownloadList.get(0);
            log.info("5分钟前正在下载：" + downLoadName);
            log.info("5分钟前文件大小：" + downLoadNowSize);
            //如果正在下载文件，则判断是否下载中断
            if (!StringUtils.isEmpty(dayDownLoadInfo.getDownloadNow())) {
                File file = new File(dayDownLoadInfo.getDownloadNow());
                Integer flag = 0;
                //如果文件存在，并且下载大小不变，则说明下载中断
                if (file.exists()) {
                    if (dayDownLoadInfo.getDownloadNow().equals(downLoadName) && file.length() == downLoadNowSize) {
                        log.info(downLoadName + "已经下载大小：" + file.length());
                        log.info("该文件阻塞后续下载，程序将断开ftp重新连接下载：" + dayDownLoadInfo.getDownloadNow() + "，任务名：" + dayDownLoadInfo.getDownName());
                        try {
                            dayDownLoadInfo.setStatus(-1);
                            dayDownLoadInfo.setReason("有一个文件阻塞下载，导致下载程序卡住，程序稍后将尝试重新下载");
                            dayDownLoadInfo.setDownloadNow(null);
                            dayDownLoadInfo.setDownloadNowSize(null);
                            JobCommon.JOB_COMMON.addDayDownLoadInfo(dayDownLoadInfo);
                            downLoadName = "";
                            downLoadNowSize = -1L;
                            flag = 1;
                            ftpService.close();
                        } catch (IOException e) {
                            log.error("关闭ftp服务器失败，可能连接已经中断:" + DownloadCommon.DOWNLOAD_COMMON.getFtpHost() + "，任务名：" + dayDownLoadInfo.getDownName());
                        }
                    } else {
                        downLoadName = dayDownLoadInfo.getDownloadNow();
                        downLoadNowSize = file.length();
                    }
                    //如果正在下载的文件不存在，则说明下载出了问题
                } else {
                    log.info("该文件阻塞后续下载，程序将断开ftp重新连接下载：" + dayDownLoadInfo.getDownloadNow() + "，任务名：" + dayDownLoadInfo.getDownName());
                    try {
                        downLoadName = "";
                        downLoadNowSize = -1L;
                        dayDownLoadInfo.setStatus(-1);
                        dayDownLoadInfo.setReason("有一个文件阻塞下载，导致下载程序卡住，程序稍后将尝试重新下载");
                        dayDownLoadInfo.setDownloadNow(null);
                        dayDownLoadInfo.setDownloadNowSize(null);
                        JobCommon.JOB_COMMON.addDayDownLoadInfo(dayDownLoadInfo);
                        flag = 1;
                        ftpService.close();
                    } catch (IOException e) {
                        log.error(e.toString());
                        log.error("关闭ftp服务器失败，可能连接已经中断:" + DownloadCommon.DOWNLOAD_COMMON.getFtpHost() + "，任务名：" + dayDownLoadInfo.getDownName());
                    }
                }
                if (flag == 1 && DownloadCommon.DOWNLOAD_COMMON.getIfListen() == 1) {
                    String subject = "自动下载ftp文件夹任务通知";
                    String text = "尊敬的" + DownloadCommon.DOWNLOAD_COMMON.getEmailName() + "\n    您所下载的文件夹：" + dayDownLoadInfo.getDownName() +
                            "，由于网络不稳定，导致程序进程阻塞，请重新启动项目恢复下载。";
                    try {
                        sendMailService.sendTextMail(subject, text, DownloadCommon.DOWNLOAD_COMMON.getFromEmail(), DownloadCommon.DOWNLOAD_COMMON.getReceivers());
                    } catch (Exception e) {
                        log.error("下载失败邮件发送失败:" + dayDownLoadInfo.getDownName());
                    }
                }
            } else {
                downLoadName = "";
                downLoadNowSize = -1L;
            }
        }
    }

    /**
     * 每隔15分钟尝试下载一次文件不存在的任务，并核查一次下载成功的任务（为了提高效率仅对当天的文件夹进行核查）
     */
    @Scheduled(cron = "0 0/15 * * * ?")
    public void checkNoExistTask() {
        log.info("开始检查是否有ftp服务器上文件不存在的任务");
        List<DayDownLoadInfo> unExistList = JobCommon.JOB_COMMON.getDayDownLoadInfoByStatus(-3);
        if (unExistList.size() != 0) {
            try {
                ftpService.open();
                ftpService.isConnected();
                for (DayDownLoadInfo dayDownLoadInfo : unExistList) {
                    if (ftpService.testChangeWorkingDirectory(DownloadCommon.DOWNLOAD_COMMON.getFtpPath() + dayDownLoadInfo.getDownName())) {
                        //如果有正在下载或者正在核查的任务，或者有正在上传或者上传核查中的任务，则等待，没有则下载
                        if (!(JobCommon.JOB_COMMON.getDayDownLoadInfoByStatus(1).size() != 0 || JobCommon.JOB_COMMON.getDayDownLoadInfoByStatus(2).size() != 0 ||
                                JobCommon.JOB_COMMON.getListByStatusAndUploadStatus(3, 1).size() != 0 ||
                                JobCommon.JOB_COMMON.getListByStatusAndUploadStatus(3, 2).size() != 0)) {
                            //更改状态为下载中，调用异步下载方法
                            dayDownLoadInfo.setStatus(1);
                            dayDownLoadInfo.setUploadStatus(0);
                            dayDownLoadInfo.setSendTimes(0);
                            JobCommon.JOB_COMMON.addDayDownLoadInfo(dayDownLoadInfo);
                            log.info("开始下载文件夹：" + dayDownLoadInfo.getDownName());
                            downLoadService.downLoadFromFtp(dayDownLoadInfo.getDownName());
                        } else {
                            //如果文件夹存在，且有正在下载的任务，则将其加入下载队列，使其等候下载
                            dayDownLoadInfo.setStatus(0);
                            dayDownLoadInfo.setSendTimes(0);
                            JobCommon.JOB_COMMON.addDayDownLoadInfo(dayDownLoadInfo);
                        }
                        break;
                    }
                }
            } catch (IOException e) {
                log.error("ftp服务器无法连接:" + DownloadCommon.DOWNLOAD_COMMON.getFtpHost());
            }
        }
        log.info("开始核查下载成功的特定日期的文件");
        List<DayDownLoadInfo> successList = JobCommon.JOB_COMMON.getDayDownLoadInfoByStatus(3);
        Integer downDate = DownloadCommon.DOWNLOAD_COMMON.getDownDate();
        //获得当日所下载上传的文件夹的名字
        LocalDate localDate = LocalDate.now().plusDays(downDate);
        String downName = localDate.toString();
        downName = downName.replace("-", "");
        if (null != successList && successList.size() != 0) {
            for (DayDownLoadInfo dayDownLoadInfo : successList) {
                //如果今日文件下载完成，并且不处于上传中或者上传核查中，则重新检查是否和ftp中的内容一致
                if (dayDownLoadInfo.getDownName().equals(downName) && dayDownLoadInfo.getUploadStatus() != 1 && dayDownLoadInfo.getUploadStatus() != 2) {
                    DayDownLoadInfo checkInfo = downLoadService.checkDownLoadByDownName(downName);
                    List<String> missPaths = checkInfo.getMissPath();
                    //如果有缺失的路径则重新下载上传
                    if (null != missPaths && missPaths.size() != 0) {
                        dayDownLoadInfo.setStatus(0);
                        dayDownLoadInfo.setUploadStatus(0);
                        JobCommon.JOB_COMMON.addDayDownLoadInfo(dayDownLoadInfo);
                    }
                }
            }
        }
    }

    /**
     * 每隔2分钟查一次任务列表
     */
    @Scheduled(cron = "0 0/2 * * * ?")
    public void checkDownLoadTask() {
        log.debug("检查所有下载任务");
        log.debug("检查下载成功的任务");
        List<DayDownLoadInfo> successList = JobCommon.JOB_COMMON.getDayDownLoadInfoByStatus(3);
        if (successList.size() != 0) {
            for (DayDownLoadInfo successDownLoad : successList) {
                //如果文件下载成功，只通知一次
                if (successDownLoad.getSendTimes() < 2) {
                    successDownLoad.setSendTimes(2);
                    JobCommon.JOB_COMMON.addDayDownLoadInfo(successDownLoad);
                    downLoadCommonService.saveDownLoadInfoToFile();
                    //如果不需要对下载任务进行监听则不发送邮件
                    if (DownloadCommon.DOWNLOAD_COMMON.getIfListen() == 0) {
                        break;
                    }
                    String subject = "自动下载ftp文件夹任务通知";
                    String text = "尊敬的" + DownloadCommon.DOWNLOAD_COMMON.getEmailName() + "\n    您所下载的文件夹：" + successDownLoad.getDownName() + "已经下载并核对完成。";
                    log.info("该文件夹已经下载完毕：" + successDownLoad.getDownName());
                    try {
                        sendMailService.sendTextMail(subject, text, DownloadCommon.DOWNLOAD_COMMON.getFromEmail(), DownloadCommon.DOWNLOAD_COMMON.getReceivers());
                    } catch (Exception e) {
                        log.error("下载成功邮件发送异常:" + successDownLoad.getDownName());
                    }
                }
            }
        }
        log.debug("检查下载失败或者未下载的任务");
        List<DayDownLoadInfo> unLoadList = JobCommon.JOB_COMMON.getDayDownLoadInfoByStatus(0);
        unLoadList.addAll(JobCommon.JOB_COMMON.getDayDownLoadInfoByStatus(-1));
        if (unLoadList.size() != 0) {
            for (DayDownLoadInfo dayDownLoadInfo : unLoadList) {
                log.debug("需要下载的任务：" + dayDownLoadInfo.getDownName());
                //对于下载失败的任务进行通知
                if (dayDownLoadInfo.getSendTimes() == 0 && dayDownLoadInfo.getStatus() == -1) {
                    dayDownLoadInfo.setSendTimes(1);
                    dayDownLoadInfo.setReason("");
                    JobCommon.JOB_COMMON.addDayDownLoadInfo(dayDownLoadInfo);
                    downLoadCommonService.saveDownLoadInfoToFile();
                    //由于统计下载上传情况可能导致下载上传异常，故不进行通知
                    //如果需要对下载任务进行监听则执行逻辑
//                    if (Common.COMMON.getIfListen() == 1) {
//                        String subject = "自动下载ftp文件夹任务通知";
//                        String text = "尊敬的" + Common.COMMON.getBroadName() + "\n    您所下载的文件夹：" + dayDownLoadInfo.getDownName() + "由于断网、停电、ftp不稳定，或者登陆ftp服务器失败等因素导致下载失败，" +
//                                "为了确保文件能够下载完成，建议您能够前往查看网络环境、ftp用户名密码是否正确，重新启动项目恢复下载。";
//                        try {
//                            //由于凤凰ftp不稳定，每次下载都会出现-1状态，故不发送邮件通知
//                            sendMailService.sendTextMail(subject, text, Common.COMMON.getFromEmail(), Common.COMMON.getBroadReceiver());
//                        } catch (Exception e) {
//                            log.error("下载失败邮件发送失败:" + dayDownLoadInfo.getDownName());
//                        }
//                    }
                }
                //如果有正在下载或者正在核查的任务，或者有正在上传或者上传核查中的任务，则等待，没有则下载
                if (JobCommon.JOB_COMMON.getDayDownLoadInfoByStatus(1).size() != 0 || JobCommon.JOB_COMMON.getDayDownLoadInfoByStatus(2).size() != 0 ||
                        JobCommon.JOB_COMMON.getListByStatusAndUploadStatus(3, 1).size() != 0 ||
                        JobCommon.JOB_COMMON.getListByStatusAndUploadStatus(3, 2).size() != 0) {
                    continue;
                }
                try {
                    //尝试连接凤凰ftp
                    ftpService.open();
                    ftpService.isConnected();
                    log.info("开始下载文件夹：" + dayDownLoadInfo.getDownName());
                    //如果连接成功，则添加下载文件,变更状态为下载中
                    dayDownLoadInfo.setStatus(1);
                    dayDownLoadInfo.setUploadStatus(0);
                    dayDownLoadInfo.setReason("");
                    JobCommon.JOB_COMMON.addDayDownLoadInfo(dayDownLoadInfo);
                    downLoadService.downLoadFromFtp(dayDownLoadInfo.getDownName());
                    break;
                } catch (IOException e) {
                    log.error("无法连接或者登陆ftp服务器:" + DownloadCommon.DOWNLOAD_COMMON.getFtpHost());
                }
            }
        }
    }
}
