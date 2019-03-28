package com.wangyb.ftpdemo.service;

import com.wangyb.ftpdemo.config.DownloadCommon;
import com.wangyb.ftpdemo.config.StatisticsCommon;
import com.wangyb.ftpdemo.controller.response.ResponseInfo;
import com.wangyb.ftpdemo.pojo.DayDownLoadInfo;
import com.wangyb.ftpdemo.pojo.JobCommon;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with Intellij IDEA.
 *
 * @author wangyb
 * @Date 2019/1/29 14:23
 * Modified By:
 * Description:
 */
@Service
@Slf4j
public class DownLoadService {

    private final FtpService ftpService;

    private final DownLoadCommonService downLoadCommonService;


    @Autowired
    public DownLoadService(FtpService ftpService, DownLoadCommonService downLoadCommonService) {
        this.ftpService = ftpService;
        this.downLoadCommonService = downLoadCommonService;
    }

    @Async("taskExecutor")
    public void downLoadFromFtp(String downName) {
        //为该日期建立下载记录
        DayDownLoadInfo dayDownLoadInfo = new DayDownLoadInfo(downName, 0);
        dayDownLoadInfo.setReason("");
        //更新状态为下载中
        dayDownLoadInfo.setStatus(1);
        JobCommon.JOB_COMMON.addDayDownLoadInfo(dayDownLoadInfo);
        //将下载任务进行持久化，防止下载中断电，重启后任务丢失
        downLoadCommonService.saveDownLoadInfoToFile();
        try {
            ftpService.open();
            //判断能否切换到ftp指定路径
            String filePath = DownloadCommon.DOWNLOAD_COMMON.getFtpPath() + downName;
            String destPath = DownloadCommon.DOWNLOAD_COMMON.getDestinationPath() + downName + "/";
            if (ftpService.testChangeWorkingDirectory(DownloadCommon.DOWNLOAD_COMMON.getFtpPath())) {
                List<String> nameString = ftpService.listFiles(DownloadCommon.DOWNLOAD_COMMON.getFtpPath());
                if (null != nameString && nameString.size() != 0) {
                    //判断是否有要更新的文件夹，如果有文件则进行下载
                    if (nameString.contains(downName)) {
                        try {
                            //获取文件大小及数量
                            Long fileSize = ftpService.getFileSize(filePath);
                            Integer fileNumber = ftpService.getFileNumber(filePath);
                            log.info(downName + "下载任务总大小：" + fileSize);
                            log.info(downName + "下载任务总文件数：" + fileNumber);
                            dayDownLoadInfo.setDownLoadTotal(fileSize);
                            dayDownLoadInfo.setFileNumber(fileNumber);
                            JobCommon.JOB_COMMON.addDayDownLoadInfo(dayDownLoadInfo);
                            //如果目标路径没有
                            File file = new File(destPath);
                            if (!file.exists()) {
                                file.mkdirs();
                            }
                            log.info("开始下载每日更新文件：" + downName);
                            ftpService.downloadDirectory(filePath, destPath, dayDownLoadInfo, 0);
                            dayDownLoadInfo = JobCommon.JOB_COMMON.getDownNameSameInfo(downName);
                            if (null == dayDownLoadInfo) {
                                dayDownLoadInfo = new DayDownLoadInfo(downName, 1);
                            }
                            log.debug("已下载文件大小：" + dayDownLoadInfo.getLoadedTotal());
                            log.debug("已下载个数：" + dayDownLoadInfo.getLoadedFileNumber());
                            //更新状态为检查中
                            dayDownLoadInfo.setStatus(2);
                            //检查前清空丢失文件列表
                            dayDownLoadInfo.setMissPath(new ArrayList<>());
                            JobCommon.JOB_COMMON.addDayDownLoadInfo(dayDownLoadInfo);
                            log.info("开始核查每日更新文件：" + downName);
                            ftpService.checkDownLoadFile(filePath, destPath, dayDownLoadInfo);
                            DayDownLoadInfo downLoadInfo = JobCommon.JOB_COMMON.getDownNameSameInfo(downName);
                            if (null != downLoadInfo) {
                                if (downLoadInfo.getMissPath().size() != 0) {
                                    log.info("文件下载不完整，程序将重新尝试下载:" + downName);
                                    dayDownLoadInfo.setReason("文件不完整，正在尝试补充下载");
                                    JobCommon.JOB_COMMON.addDayDownLoadInfo(dayDownLoadInfo);
                                    //如果文件下载不完整则重新下载
                                    downLoadFromFtp(downName);
                                } else {
                                    //更新文件状态为下载并核对完成
                                    dayDownLoadInfo.setStatus(3);
                                    dayDownLoadInfo.setReason("");
                                    JobCommon.JOB_COMMON.addDayDownLoadInfo(dayDownLoadInfo);
                                    log.info(downName + "文件下载完成");
                                }
                            } else {
                                dayDownLoadInfo = new DayDownLoadInfo(downName, 1);
                            }
                        } catch (IOException e) {
                            log.error("下载文件夹出错：" + dayDownLoadInfo.getDownName());
                            //更新文件状态为下载失败
                            dayDownLoadInfo.setStatus(-1);
                            dayDownLoadInfo.setReason("文件下载失败，可能是网络中断、ftp无下载反馈的原因,程序将尝试恢复下载");
                            JobCommon.JOB_COMMON.addDayDownLoadInfo(dayDownLoadInfo);
                        } catch (NullPointerException e) {
                            dayDownLoadInfo = JobCommon.JOB_COMMON.getDownNameSameInfo(downName);
                            if (null == dayDownLoadInfo) {
                                dayDownLoadInfo = new DayDownLoadInfo(downName, 1);
                            }
                            downLoadFromFtp(downName);
                        }
                    } else {
                        dayDownLoadInfo.setStatus(-3);
                        dayDownLoadInfo.setReason("ftp中" + DownloadCommon.DOWNLOAD_COMMON.getFtpPath() + "文件夹下没有" + downName + "文件");
                    }
                } else {
                    dayDownLoadInfo.setStatus(-3);
                    dayDownLoadInfo.setReason("ftp" + DownloadCommon.DOWNLOAD_COMMON.getFtpPath() + "文件夹下没有文件");
                }
            } else {
                dayDownLoadInfo.setStatus(-3);
                dayDownLoadInfo.setReason("ftp访问" + DownloadCommon.DOWNLOAD_COMMON.getFtpPath() + "文件夹失败，请确认ftp上是否存在该文件夹");
            }
            JobCommon.JOB_COMMON.addDayDownLoadInfo(dayDownLoadInfo);
        } catch (IOException e) {
            log.error("登录ftp失败");
            dayDownLoadInfo.setStatus(-1);
            dayDownLoadInfo.setReason("连接或者登录ftp失败");
        } finally {
            JobCommon.JOB_COMMON.addDayDownLoadInfo(dayDownLoadInfo);
        }
        log.debug("本轮下载结束");
        //防止下载卡住
        if (dayDownLoadInfo.getStatus() == 1 || dayDownLoadInfo.getStatus() == 2) {
            dayDownLoadInfo.setStatus(0);
        }
        JobCommon.JOB_COMMON.addDayDownLoadInfo(dayDownLoadInfo);
        //将下载记录进行持久化
        downLoadCommonService.saveDownLoadInfoToFile();
    }

    /**
     * 检查某个文件夹的完整性
     *
     * @param downName 文件夹名
     * @return
     */
    public DayDownLoadInfo checkDownLoadByDownName(String downName) {
        Boolean ifNotExist = JobCommon.JOB_COMMON.getDownNameSameInfo(downName) == null;
        DayDownLoadInfo dayDownLoadInfo = new DayDownLoadInfo(downName, 1);
        JobCommon.JOB_COMMON.addDayDownLoadInfo(dayDownLoadInfo);
        try {
            ftpService.open();
            //判断能否切换到ftp指定路径
            String filePath = DownloadCommon.DOWNLOAD_COMMON.getFtpPath() + downName;
            String destPath = DownloadCommon.DOWNLOAD_COMMON.getDestinationPath() + downName + "/";
            if (ftpService.testChangeWorkingDirectory(DownloadCommon.DOWNLOAD_COMMON.getFtpPath())) {
                List<String> nameString = ftpService.listFiles(DownloadCommon.DOWNLOAD_COMMON.getFtpPath());
                if (null != nameString && nameString.size() != 0) {
                    //判断是否有要更新的文件夹，如果有文件则进行检查
                    if (nameString.contains(downName)) {
                        //如果目标路径没有
                        File file = new File(destPath);
                        if (!file.exists()) {
                            file.mkdirs();
                        }
                        //检查前清空丢失文件路径列表
                        dayDownLoadInfo.setMissPath(new ArrayList<>());
                        ftpService.checkDownLoadFile(filePath, destPath, dayDownLoadInfo);
                        DayDownLoadInfo downLoadInfo = JobCommon.JOB_COMMON.getDownNameSameInfo(downName);
                        if (null != downLoadInfo) {
                            dayDownLoadInfo = new DayDownLoadInfo(downLoadInfo);
                            //如果该文件不是正在核查中，则清空missPath
                            if (downLoadInfo.getStatus() != 2) {
                                downLoadInfo.setMissPath(new ArrayList<>());
                                JobCommon.JOB_COMMON.addDayDownLoadInfo(downLoadInfo);
                            }
                            if (dayDownLoadInfo.getMissPath().size() != 0) {
                                dayDownLoadInfo.setStatus(-2);
                                dayDownLoadInfo.setReason("下载文件不完整");
                            } else {
                                //下载完整
                                dayDownLoadInfo.setStatus(3);
                            }
                        }
                    } else {
                        dayDownLoadInfo.setStatus(-3);
                        dayDownLoadInfo.setReason("ftp中" + DownloadCommon.DOWNLOAD_COMMON.getFtpPath() + "文件夹下没有" + downName + "文件");
                    }
                } else {
                    dayDownLoadInfo.setStatus(-3);
                    dayDownLoadInfo.setReason("ftp" + DownloadCommon.DOWNLOAD_COMMON.getFtpPath() + "文件夹下没有文件");
                }
            } else {
                dayDownLoadInfo.setStatus(-3);
                dayDownLoadInfo.setReason("ftp访问" + DownloadCommon.DOWNLOAD_COMMON.getFtpPath() + "文件夹失败，请确认ftp上是否存在该文件夹");
            }
        } catch (IOException e) {
            log.error("登录ftp失败");
            dayDownLoadInfo.setStatus(-1);
            dayDownLoadInfo.setReason("连接或者登录ftp失败");
        }
        //系统中不保存检查信息，防止因为检查数据过多将下载历史数据挤出列表
        if (ifNotExist) {
            JobCommon.JOB_COMMON.removeDayDownLoadInfo(downName);
            //防止下载任务执行写入文件操作，这里重新写入数据
            downLoadCommonService.saveDownLoadInfoToFile();
        }
        return dayDownLoadInfo;
    }

    /**
     * 添加下载文件夹的任务
     *
     * @param downName 要下载的文件夹的名字
     * @return
     */
    public ResponseInfo putDownLoadJob(String downName) {
        DayDownLoadInfo dayDownLoadInfo = JobCommon.JOB_COMMON.getDownNameSameInfo(downName);
        ResponseInfo responseInfo = new ResponseInfo();
        //如果有下载任务则更新通知次数
        if (null != dayDownLoadInfo) {
            //如果正在下载或者核查，则不管
            if (dayDownLoadInfo.getStatus() == 1 || dayDownLoadInfo.getStatus() == 2) {
                responseInfo.setIfOk(true);
                responseInfo.setReason(downName + "正在下载中，请耐心等待");
                return responseInfo;
            } else {
                if (dayDownLoadInfo.getStatus() == 3 && dayDownLoadInfo.getUploadStatus() != 1 && dayDownLoadInfo.getUploadStatus() != 2) {
                    dayDownLoadInfo.setReason("等待下载核查");
                    dayDownLoadInfo.setUploadStatus(0);
                    responseInfo.setIfOk(true);
                    responseInfo.setReason(downName + "已经下载完毕，程序将重新核查下载");
                } else {
                    if (dayDownLoadInfo.getStatus() == 3 && (dayDownLoadInfo.getUploadStatus() == 1 || dayDownLoadInfo.getUploadStatus() == 2)) {
                        responseInfo.setIfOk(false);
                        responseInfo.setReason(downName + "正在上传中，请耐心等待上传");
                        return responseInfo;
                    }
                    dayDownLoadInfo.setReason("等待下载");
                    responseInfo.setIfOk(true);
                    responseInfo.setReason(downName + "已经变更到未下载队列，稍后将进行下载");
                }
            }
            //清除通知状态
            dayDownLoadInfo.setSendTimes(0);
            dayDownLoadInfo.setStatus(0);
            dayDownLoadInfo.setDownLoadTotal(0L);
            dayDownLoadInfo.setLoadedTotal(0L);
            dayDownLoadInfo.setFileNumber(0);
            dayDownLoadInfo.setLoadedFileNumber(0);
            dayDownLoadInfo.setUploadTotal(0L);
            dayDownLoadInfo.setUploadedTotal(0L);
            dayDownLoadInfo.setUploadFileNumber(0);
            dayDownLoadInfo.setUploadedFileNumber(0);
            JobCommon.JOB_COMMON.addDayDownLoadInfo(dayDownLoadInfo);
        } else {
            DayDownLoadInfo downLoadInfo = new DayDownLoadInfo(downName, 0);
            downLoadInfo.setReason("等待下载");
            JobCommon.JOB_COMMON.addDayDownLoadInfo(downLoadInfo);
            responseInfo.setIfOk(true);
            responseInfo.setReason(downName + "已经添加到下载队列，稍后将进行下载");
        }
        log.info("添加下载任务成功：" + downName);
        downLoadCommonService.saveDownLoadInfoToFile();
        return responseInfo;
    }

    public void checkAndDownLoadFile() {
        List<DayDownLoadInfo> downLoadInfoList = JobCommon.JOB_COMMON.getAllDownLoadInfo();
        if (downLoadInfoList.size() != 0) {
            List<DayDownLoadInfo> list = new ArrayList<>(downLoadInfoList);
            for (DayDownLoadInfo dayDownLoadInfo : list) {
                if (dayDownLoadInfo.getStatus() != 3) {
                    //将通知次数重置
                    dayDownLoadInfo.setSendTimes(0);
                    //标记下载任务的状态为等待下载
                    dayDownLoadInfo.setStatus(0);
                    dayDownLoadInfo.setDownloadNow(null);
                    dayDownLoadInfo.setDownloadNowSize(null);
                    dayDownLoadInfo.setReason("等待下载");
                    dayDownLoadInfo.setMissPath(new ArrayList<>());
                    dayDownLoadInfo.setUploadStatus(0);
                    dayDownLoadInfo.setUploadReason("");
                    dayDownLoadInfo.setUploadMissPath(new ArrayList<>());
                    dayDownLoadInfo.setUploadNow(null);
                    dayDownLoadInfo.setUploadNowSize(null);
                    dayDownLoadInfo.setDownLoadTotal(0L);
                    dayDownLoadInfo.setLoadedTotal(0L);
                    dayDownLoadInfo.setFileNumber(0);
                    dayDownLoadInfo.setLoadedFileNumber(0);
                    dayDownLoadInfo.setUploadTotal(0L);
                    dayDownLoadInfo.setUploadedTotal(0L);
                    dayDownLoadInfo.setUploadFileNumber(0);
                    dayDownLoadInfo.setUploadedFileNumber(0);
                    JobCommon.JOB_COMMON.addDayDownLoadInfo(dayDownLoadInfo);
                    log.info("已经添加下载任务到定时任务中：" + dayDownLoadInfo.getDownName());
                }
            }
        }
    }

    /**
     * 移除某个下载任务
     *
     * @param downName
     * @return
     */
    public Boolean removeOneDownLoadTask(String downName) {
        DayDownLoadInfo dayDownLoadInfo = JobCommon.JOB_COMMON.getDownNameSameInfo(downName);
        if (null == dayDownLoadInfo) {
            return true;
        } else {
            //如果不是正在下载、上传或者核查文件则移除任务
            if (dayDownLoadInfo.getStatus() != 1 && dayDownLoadInfo.getStatus() != 2 && dayDownLoadInfo.getUploadStatus() != 1
                    && dayDownLoadInfo.getUploadStatus() != 2) {
                JobCommon.JOB_COMMON.removeDayDownLoadInfo(downName);
                downLoadCommonService.saveDownLoadInfoToFile();
                log.info("删除任务成功：" + downName);
                return true;
            } else {
                return false;
            }
        }
    }

    public void statisticsDownload() {
        try {
            ftpService.open();
            String filePath = DownloadCommon.DOWNLOAD_COMMON.getFtpPath() + StatisticsCommon.STATISTICS_COMMON.getDownName();
            String destPath = DownloadCommon.DOWNLOAD_COMMON.getDestinationPath() + StatisticsCommon.STATISTICS_COMMON.getDownName() + "/";
            if (ftpService.testChangeWorkingDirectory(DownloadCommon.DOWNLOAD_COMMON.getFtpPath())) {
                List<String> nameString = ftpService.listFiles(DownloadCommon.DOWNLOAD_COMMON.getFtpPath());
                if (null != nameString && nameString.size() != 0) {
                    //判断是否有要更新的文件夹，如果有文件则进行检查
                    if (nameString.contains(StatisticsCommon.STATISTICS_COMMON.getDownName())) {
                        //如果目标路径没有
                        File file = new File(destPath);
                        if (!file.exists()) {
                            file.mkdirs();
                        }
                        if (ftpService.testChangeWorkingDirectory(filePath + "/")) {
                            ftpService.statisticDownloadFile(filePath, destPath);
                        }
                    } else {
                        StatisticsCommon.STATISTICS_COMMON.setDownloadStatus(-3);
                        log.info("文件夹不存在：" + StatisticsCommon.STATISTICS_COMMON.getDownName());
                    }
                } else {
                    StatisticsCommon.STATISTICS_COMMON.setDownloadStatus(-3);
                    log.info("文件夹不存在：" + StatisticsCommon.STATISTICS_COMMON.getDownName());
                }
            } else {
                StatisticsCommon.STATISTICS_COMMON.setDownloadStatus(-3);
                log.info("文件夹不存在：" + StatisticsCommon.STATISTICS_COMMON.getDownName());
            }
        } catch (IOException e) {
            StatisticsCommon.STATISTICS_COMMON.setDownloadStatus(-1);
            log.info("登录凤凰ftp失败");
        }
    }
}
