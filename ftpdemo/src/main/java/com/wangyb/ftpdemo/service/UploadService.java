package com.wangyb.ftpdemo.service;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.wangyb.ftpdemo.config.DownloadCommon;
import com.wangyb.ftpdemo.config.StatisticsCommon;
import com.wangyb.ftpdemo.config.UploadCommon;
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
 * @Date 2019/2/15 17:14
 * Modified By:
 * Description:
 */
@Service
@Slf4j
public class UploadService {

    private final UploadFtpService uploadFtpService;

    private final DownLoadCommonService downLoadCommonService;

    private final Gson gson;

    @Autowired
    public UploadService(UploadFtpService uploadFtpService, DownLoadCommonService downLoadCommonService, Gson gson) {
        this.uploadFtpService = uploadFtpService;
        this.downLoadCommonService = downLoadCommonService;
        this.gson = gson;
    }

    /**
     * 上传本地文件到ftp中
     *
     * @param dayDownLoadInfo 任务信息
     */
    @Async("taskExecutor")
    public void uploadFileToFtp(DayDownLoadInfo dayDownLoadInfo) {
        String downName = dayDownLoadInfo.getDownName();
        //修改状态为上传中
        dayDownLoadInfo.setUploadStatus(1);
        dayDownLoadInfo.setUploadReason("");
        dayDownLoadInfo.setUploadedTotal(0L);
        dayDownLoadInfo.setUploadedFileNumber(0);
        dayDownLoadInfo.setUploadMissPath(new ArrayList<>());
        //改变任务的优先级，防止每次都上传该文件夹
        JobCommon.JOB_COMMON.addDayDownLoadInfo(dayDownLoadInfo);
        downLoadCommonService.saveDownLoadInfoToFile();
        File file = new File(DownloadCommon.DOWNLOAD_COMMON.getDestinationPath() + dayDownLoadInfo.getDownName());
        if (!file.exists()) {
            dayDownLoadInfo.setUploadStatus(-3);
            dayDownLoadInfo.setUploadReason("本地文件夹不存在：" + DownloadCommon.DOWNLOAD_COMMON.getDestinationPath() + dayDownLoadInfo.getDownName());
        } else {
            if (!file.isDirectory()) {
                dayDownLoadInfo.setUploadStatus(-3);
                dayDownLoadInfo.setUploadReason("本地文件夹不存在：" + DownloadCommon.DOWNLOAD_COMMON.getDestinationPath() + dayDownLoadInfo.getDownName() + "，该路径不是文件夹而是文件。");
                return;
            }
            Integer flag = 0;
            dayDownLoadInfo.setUploadTotal(getUploadTotalSize(DownloadCommon.DOWNLOAD_COMMON.getDestinationPath() + dayDownLoadInfo.getDownName()));
            dayDownLoadInfo.setUploadFileNumber(getUploadTotalNum(DownloadCommon.DOWNLOAD_COMMON.getDestinationPath() + dayDownLoadInfo.getDownName()));
            //如果要上传文件则上传文件
            if (UploadCommon.UPLOAD_COMMON.getIfUpload() == 1) {
                log.info("开始上传文件:" + dayDownLoadInfo.getDownName());
                try {
                    uploadFtpService.open(UploadCommon.UPLOAD_COMMON.getFtpHost(), UploadCommon.UPLOAD_COMMON.getFtpPort(),
                            UploadCommon.UPLOAD_COMMON.getFtpUserName(), UploadCommon.UPLOAD_COMMON.getFtpPassword());
                    try {
                        dayDownLoadInfo.setUploadStatus(1);
                        JobCommon.JOB_COMMON.addDayDownLoadInfo(dayDownLoadInfo);
                        uploadFtpService.uploadDirectory(DownloadCommon.DOWNLOAD_COMMON.getDestinationPath() + dayDownLoadInfo.getDownName() + "/",
                                UploadCommon.UPLOAD_COMMON.getFtpPath() + dayDownLoadInfo.getDownName() + "/", dayDownLoadInfo, 0);
                        dayDownLoadInfo.setUploadStatus(2);
                        JobCommon.JOB_COMMON.addDayDownLoadInfo(dayDownLoadInfo);
                        log.info("开始核查文件：" + dayDownLoadInfo.getDownName());
                        uploadFtpService.checkDirectory(DownloadCommon.DOWNLOAD_COMMON.getDestinationPath() + dayDownLoadInfo.getDownName() + "/adi/",
                                UploadCommon.UPLOAD_COMMON.getFtpPath() + dayDownLoadInfo.getDownName() + "/adi/", dayDownLoadInfo);
                        dayDownLoadInfo = JobCommon.JOB_COMMON.getDownNameSameInfo(dayDownLoadInfo.getDownName());
                        if (null == dayDownLoadInfo) {
                            dayDownLoadInfo = new DayDownLoadInfo(downName, 1);
                        } else {
                            if (dayDownLoadInfo.getUploadMissPath().size() > 0) {
                                uploadFileToFtp(dayDownLoadInfo);
                            }
                        }
                    } catch (IOException e) {
                        JobCommon.JOB_COMMON.addDayDownLoadInfo(dayDownLoadInfo);
                        uploadFileToFtp(dayDownLoadInfo);
                    } catch (NullPointerException e) {
                        dayDownLoadInfo = JobCommon.JOB_COMMON.getDownNameSameInfo(downName);
                        if (null != dayDownLoadInfo) {
                            uploadFileToFtp(dayDownLoadInfo);
                        } else {
                            dayDownLoadInfo = new DayDownLoadInfo(downName, 1);
                        }
                    }
                } catch (IOException e) {
                    log.error("登录ftp失败");
                    flag = 3;
                }
            }
            if (flag == 0) {
                dayDownLoadInfo.setUploadStatus(3);
                dayDownLoadInfo.setUploadReason("");
                log.info("该文件上传完毕：" + dayDownLoadInfo.getDownName());
            }
            if (flag == 3) {
                dayDownLoadInfo.setUploadStatus(-1);
                dayDownLoadInfo.setUploadReason("登录ftp失败，或者上传文件出错，程序稍后将尝试重新上传");
            }
        }
        log.debug("本轮上传结束");
        //防止上传被卡住
        if (dayDownLoadInfo.getUploadStatus() == 1 || dayDownLoadInfo.getUploadStatus() == 2) {
            dayDownLoadInfo.setUploadStatus(0);
        }
        JobCommon.JOB_COMMON.addDayDownLoadInfo(dayDownLoadInfo);
        //将上传信息存入文件
        downLoadCommonService.saveDownLoadInfoToFile();
    }


    /**
     * 添加上传任务
     *
     * @param downName 任务名称
     * @return
     */
    public ResponseInfo addUploadJob(String downName) {
        ResponseInfo responseInfo = new ResponseInfo();
        DayDownLoadInfo dayDownLoadInfo = JobCommon.JOB_COMMON.getDownNameSameInfo(downName);
        //如果任务列表中有对应的任务，则进行判断处理
        if (null != dayDownLoadInfo) {
            if (dayDownLoadInfo.getStatus() != 3) {
                responseInfo.setIfOk(false);
                responseInfo.setReason(downName + "文件夹没有下载完毕，请耐心等待，下载完毕后将进行上传");
            }
            if (dayDownLoadInfo.getStatus() == 3 && dayDownLoadInfo.getUploadStatus() != 1 && dayDownLoadInfo.getUploadStatus() != 2) {
                if (dayDownLoadInfo.getUploadStatus() == 3) {
                    responseInfo.setIfOk(true);
                    responseInfo.setReason(downName + "文件夹已经上传过了，程序将再次核查上传");
                    dayDownLoadInfo.setSendTimes(2);
                } else {
                    responseInfo.setIfOk(true);
                    responseInfo.setReason(downName + "文件夹已经添加到上传队列中");
                }
                //变更状态为等待上传
                dayDownLoadInfo.setUploadStatus(0);
            }
            if (dayDownLoadInfo.getStatus() == 3 && (dayDownLoadInfo.getUploadStatus() == 1 || dayDownLoadInfo.getUploadStatus() == 2)) {
                responseInfo.setIfOk(true);
                responseInfo.setReason(downName + "文件夹正在上传中，请耐心等待");
            }
            //清除原来的统计数据
            dayDownLoadInfo.setUploadFileNumber(null);
            dayDownLoadInfo.setUploadTotal(null);
            dayDownLoadInfo.setUploadedFileNumber(null);
            dayDownLoadInfo.setUploadedTotal(null);
            JobCommon.JOB_COMMON.addDayDownLoadInfo(dayDownLoadInfo);
        } else {
            File file = new File(DownloadCommon.DOWNLOAD_COMMON.getDestinationPath() + downName);
            if (file.exists()) {
                dayDownLoadInfo = new DayDownLoadInfo(downName);
                dayDownLoadInfo.setSendTimes(2);
                JobCommon.JOB_COMMON.addDayDownLoadInfo(dayDownLoadInfo);
                responseInfo.setIfOk(true);
                responseInfo.setReason(downName + "文件夹已经添加到上传队列中");
            } else {
                responseInfo.setIfOk(false);
                responseInfo.setReason(downName + "文件夹不存在，请先添加下载任务或者将文件夹拷贝到" + DownloadCommon.DOWNLOAD_COMMON.getDestinationPath() + "目录下");
            }
        }
        log.info("添加上传任务成功：" + downName);
        downLoadCommonService.saveDownLoadInfoToFile();
        return responseInfo;
    }

    /**
     * 检查ftp中的文件是否和本地相同，如果缺失文件，则记录返回
     *
     * @param downName 文件夹名
     * @return
     */
    public DayDownLoadInfo checkUploadByDownName(String downName) {
        Boolean ifNotExist = JobCommon.JOB_COMMON.getDownNameSameInfo(downName) == null;
        DayDownLoadInfo dayDownLoadInfo = new DayDownLoadInfo(downName, 1);
        JobCommon.JOB_COMMON.addDayDownLoadInfo(dayDownLoadInfo);
        File file = new File(DownloadCommon.DOWNLOAD_COMMON.getDestinationPath() + dayDownLoadInfo.getDownName());
        if (!file.exists()) {
            dayDownLoadInfo.setUploadStatus(-3);
            dayDownLoadInfo.setUploadReason("本地文件夹不存在：" + DownloadCommon.DOWNLOAD_COMMON.getDestinationPath() + dayDownLoadInfo.getDownName());
        } else {
            if (!file.isDirectory()) {
                dayDownLoadInfo.setUploadStatus(-3);
                dayDownLoadInfo.setUploadReason("本地文件夹不存在：" + DownloadCommon.DOWNLOAD_COMMON.getDestinationPath() + dayDownLoadInfo.getDownName() + "，该路径不是文件夹而是文件。");
            } else {
                Integer flag = 0;
                dayDownLoadInfo.setUploadMissPath(new ArrayList<>());
                //如果设定上传文件则检查
                if (UploadCommon.UPLOAD_COMMON.getIfUpload() == 1) {
                    try {
                        uploadFtpService.open(UploadCommon.UPLOAD_COMMON.getFtpHost(), UploadCommon.UPLOAD_COMMON.getFtpPort(),
                                UploadCommon.UPLOAD_COMMON.getFtpUserName(), UploadCommon.UPLOAD_COMMON.getFtpPassword());
                        uploadFtpService.checkDirectory(DownloadCommon.DOWNLOAD_COMMON.getDestinationPath() + dayDownLoadInfo.getDownName() + "/",
                                UploadCommon.UPLOAD_COMMON.getFtpPath() + dayDownLoadInfo.getDownName() + "/", dayDownLoadInfo);
                        dayDownLoadInfo = JobCommon.JOB_COMMON.getDownNameSameInfo(dayDownLoadInfo.getDownName());
                        if (null == dayDownLoadInfo) {
                            dayDownLoadInfo = new DayDownLoadInfo(downName, 1);
                        }
                    } catch (IOException e) {
                        log.error("登录ftp失败");
                        flag = 2;
                    }
                }
                //正常检查完成
                if (flag == 0) {
                    dayDownLoadInfo.setUploadReason("");
                    if (dayDownLoadInfo.getUploadMissPath().size() != 0) {
                        //如果不是检查中状态则清空ftp中缺失的路径信息
                        if (dayDownLoadInfo.getUploadStatus() != 2) {
                            DayDownLoadInfo downLoadInfo = new DayDownLoadInfo(dayDownLoadInfo);
                            downLoadInfo.setUploadMissPath(new ArrayList<>());
                            JobCommon.JOB_COMMON.addDayDownLoadInfo(downLoadInfo);
                        }
                        dayDownLoadInfo.setUploadStatus(-2);
                    } else {
                        dayDownLoadInfo.setUploadStatus(3);
                    }
                }
                if (flag == 2) {
                    dayDownLoadInfo.setUploadStatus(-1);
                    dayDownLoadInfo.setUploadReason("登录ftp失败");
                }
            }
        }
        //系统中不保存检查信息，防止因为检查数据过多将下载历史数据挤出列表
        if (ifNotExist) {
            JobCommon.JOB_COMMON.removeDayDownLoadInfo(downName);
            downLoadCommonService.saveDownLoadInfoToFile();
        }
        return dayDownLoadInfo;
    }

    /**
     * 用于程序启动时，将没有上传完成的任务添加到上传队列中
     */
    public void checkAndUploadFile() {
        List<DayDownLoadInfo> downLoadInfoList = JobCommon.JOB_COMMON.getAllDownLoadInfo();
        if (downLoadInfoList.size() != 0) {
            List<DayDownLoadInfo> list = new ArrayList<>(downLoadInfoList);
            for (DayDownLoadInfo dayDownLoadInfo : list) {
                if (dayDownLoadInfo.getStatus() == 3 && dayDownLoadInfo.getUploadStatus() != 3) {
                    dayDownLoadInfo.setDownloadNow(null);
                    dayDownLoadInfo.setDownloadNowSize(null);
                    dayDownLoadInfo.setUploadStatus(0);
                    dayDownLoadInfo.setUploadReason("等待上传");
                    dayDownLoadInfo.setUploadMissPath(new ArrayList<>());
                    dayDownLoadInfo.setUploadNow(null);
                    dayDownLoadInfo.setUploadNowSize(null);
                    dayDownLoadInfo.setSendTimes(2);
                    dayDownLoadInfo.setUploadTotal(0L);
                    dayDownLoadInfo.setUploadedTotal(0L);
                    dayDownLoadInfo.setUploadFileNumber(0);
                    dayDownLoadInfo.setUploadedFileNumber(0);
                    JobCommon.JOB_COMMON.addDayDownLoadInfo(dayDownLoadInfo);
                    log.info("已经添加到上传定时任务中：" + dayDownLoadInfo.getDownName());
                }
            }
        }
    }

    /**
     * 获得本地文件夹中要上传的文件的总大小
     *
     * @param pathname 本地文件路径
     * @return
     */
    private Long getUploadTotalSize(String pathname) {
        File file = new File(pathname);
        Long totalSize = 0L;
        File[] files = file.listFiles();
        if (null != files && files.length != 0) {
            for (File f : files) {
                if (f.isDirectory()) {
                    totalSize = totalSize + getUploadTotalSize(pathname + "/" + f.getName());
                } else {
                    totalSize = totalSize + f.length();
                }
            }
        }
        return totalSize;
    }

    /**
     * 获得本地文件夹中文件的个数
     *
     * @param pathname 本地文件路径
     * @return
     */
    private Integer getUploadTotalNum(String pathname) {
        File file = new File(pathname);
        Integer totalNumber = 0;
        File[] files = file.listFiles();
        if (null != files && files.length != 0) {
            for (File f : files) {
                if (f.isDirectory()) {
                    totalNumber = totalNumber + getUploadTotalNum(pathname + "/" + f.getName());
                } else {
                    totalNumber = totalNumber + 1;
                }
            }
        }
        return totalNumber;
    }

    public void statisticUpload() {
        //如果设置要上传视频等信息，则进行统计
        File file = new File(DownloadCommon.DOWNLOAD_COMMON.getDestinationPath() + StatisticsCommon.STATISTICS_COMMON.getDownName());
        if (!file.exists()) {
            StatisticsCommon.STATISTICS_COMMON.setUploadStatus(-3);
            log.info("本地文件夹不存在：" + StatisticsCommon.STATISTICS_COMMON.getDownName());
        } else {
            if (!file.isDirectory()) {
                StatisticsCommon.STATISTICS_COMMON.setUploadStatus(-3);
                log.info("本地文件夹不存在：" + StatisticsCommon.STATISTICS_COMMON.getDownName());
            } else {
                //检查视频、adi、column等文件
                if (UploadCommon.UPLOAD_COMMON.getIfUpload() == 1) {
                    try {
                        uploadFtpService.open(UploadCommon.UPLOAD_COMMON.getFtpHost(), UploadCommon.UPLOAD_COMMON.getFtpPort(),
                                UploadCommon.UPLOAD_COMMON.getFtpUserName(), UploadCommon.UPLOAD_COMMON.getFtpPassword());
                        uploadFtpService.statisticsFile(DownloadCommon.DOWNLOAD_COMMON.getDestinationPath() + StatisticsCommon.STATISTICS_COMMON.getDownName() + "/",
                                UploadCommon.UPLOAD_COMMON.getFtpPath() + StatisticsCommon.STATISTICS_COMMON.getDownName() + "/");
                    } catch (IOException e) {
                        log.error("登录ftp失败");
                        StatisticsCommon.STATISTICS_COMMON.setUploadStatus(-1);
                    }
                }
            }
        }
    }
}
