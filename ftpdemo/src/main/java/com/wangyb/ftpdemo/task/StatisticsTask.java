package com.wangyb.ftpdemo.task;

import com.wangyb.ftpdemo.config.DownloadCommon;
import com.wangyb.ftpdemo.config.StatisticsCommon;
import com.wangyb.ftpdemo.config.UploadCommon;
import com.wangyb.ftpdemo.pojo.DayDownLoadInfo;
import com.wangyb.ftpdemo.pojo.JobCommon;
import com.wangyb.ftpdemo.service.DownLoadService;
import com.wangyb.ftpdemo.service.HttpClientService;
import com.wangyb.ftpdemo.service.SendMailService;
import com.wangyb.ftpdemo.service.UploadService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with Intellij IDEA.
 *
 * @author wangyb
 * @Date 2019/3/12 14:36
 * Modified By:
 * Description:
 */
@Component
@Slf4j
public class StatisticsTask {

    private final SendMailService sendMailService;

    private final DownLoadService downloadService;

    private final UploadService uploadService;

    private final HttpClientService httpClientService;

    @Autowired
    public StatisticsTask(SendMailService sendMailService, DownLoadService downloadService,
                          UploadService uploadService, HttpClientService httpClientService) {
        this.sendMailService = sendMailService;
        this.downloadService = downloadService;
        this.uploadService = uploadService;
        this.httpClientService = httpClientService;
    }


    @Scheduled(cron = "0 35 14 * * ?")
    public void statisticsJobOne() {
        if (DownloadCommon.DOWNLOAD_COMMON.getIfListen() == 1) {
            log.info("统计今天文件的下载上传情况并发邮件给负责人：");
            statisticsAndSendEmail();
        }
    }

    @Scheduled(cron = "0 35 17 * * ?")
    public void statisticsJobTwo() {
        if (DownloadCommon.DOWNLOAD_COMMON.getIfListen() == 1) {
            log.info("统计今天文件的下载上传情况并发邮件给负责人：");
            statisticsAndSendEmail();
        }
    }

    @Scheduled(cron = "0 35 18 * * ?")
    public void statisticsJobThree() {
        if (DownloadCommon.DOWNLOAD_COMMON.getIfListen() == 1) {
            log.info("统计今天文件的下载上传情况并发邮件给负责人：");
            statisticsAndSendEmail();
        }
    }

    private void statisticsAndSendEmail() {
        //获得当天要下载的文件夹名，如20190313
        LocalDate localDate = LocalDate.now();
        String downName = localDate.toString();
        downName = downName.replace("-", "");
        //初始化
        StatisticsCommon.STATISTICS_COMMON.init(downName, 0, 0);
        //下载统计
        downloadService.statisticsDownload();
        //上传统计
        uploadService.statisticUpload();
        String subject = "文件下载上传情况统计：" + downName;
        StringBuilder htmlBuilder = new StringBuilder();
        htmlBuilder.append("<p>尊敬的" + DownloadCommon.DOWNLOAD_COMMON.getEmailName() + ":</p>");
        htmlBuilder.append("<p>&emsp;&emsp;您好！该文件夹<strong>" + downName + "</strong>的下载上传配置信息如下：</p>");
        htmlBuilder.append("<ol>\n" +
                "<li>自动上传文件：" + (UploadCommon.UPLOAD_COMMON.getIfUpload() == 1 ? "是" : "否") + "</li>\n" +
                "</ol>\n");
        DayDownLoadInfo downLoadInfo = JobCommon.JOB_COMMON.getDownNameSameInfo(downName);
        String downloadStatusString = "";
        String uploadStatusString = "";
        if (null == downLoadInfo) {
            downloadStatusString = "未添加下载任务";
            uploadStatusString = "未添加上传任务";
        } else {
            switch (downLoadInfo.getStatus()) {
                case 0:
                    downloadStatusString = "等待下载";
                    break;
                case 1:
                case 2:
                    downloadStatusString = "正在下载";
                    break;
                case 3:
                    downloadStatusString = "下载完成";
                    break;
                case -1:
                    downloadStatusString = "下载异常";
                    break;
                case -3:
                    downloadStatusString = "文件不存在";
                    break;
            }
            switch (downLoadInfo.getUploadStatus()) {
                case 0:
                    uploadStatusString = "等待上传";
                    break;
                case 1:
                case 2:
                    uploadStatusString = "正在上传";
                    break;
                case 3:
                    uploadStatusString = "上传完成";
                    break;
                case -1:
                    uploadStatusString = "上传异常";
                    break;
                case -3:
                    uploadStatusString = "文件不存在";
                    break;
            }
        }
        htmlBuilder.append("<p>&emsp;&emsp;该文件夹<strong>" + downName + "</strong>当前状态：</p>");
        htmlBuilder.append("<ul>\n" +
                "<li>下载状态：" + downloadStatusString + "</li>\n" +
                "<li>上传状态：" + uploadStatusString + "</li>\n" +
                "</ul>\n");
        htmlBuilder.append("<p>&emsp;&emsp;该文件夹<strong>" + downName + "</strong>统计状态：</p>");
        htmlBuilder.append("<ul>\n");
        if (StatisticsCommon.STATISTICS_COMMON.getDownloadStatus() == -1) {
            htmlBuilder.append("<li>下载相关统计：异常</li>\n");
        }
        if (StatisticsCommon.STATISTICS_COMMON.getDownloadStatus() == -3) {
            htmlBuilder.append("<li>下载相关统计：文件夹不存在</li>\n");
        }
        if (StatisticsCommon.STATISTICS_COMMON.getDownloadStatus() == 0) {
            htmlBuilder.append("<li>下载相关统计：正常</li>\n");
        }
        if (StatisticsCommon.STATISTICS_COMMON.getUploadStatus() == -1) {
            htmlBuilder.append("<li>上传相关统计：异常</li>\n");
        }
        if (StatisticsCommon.STATISTICS_COMMON.getUploadStatus() == -3) {
            htmlBuilder.append("<li>上传相关统计：文件夹不存在</li>\n");
        }
        if (StatisticsCommon.STATISTICS_COMMON.getUploadStatus() == 0) {
            htmlBuilder.append("<li>上传相关统计：正常</li>\n");
        }
        htmlBuilder.append("</ul>\n");
        LocalTime localTime = LocalTime.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        String time = localTime.format(dateTimeFormatter);
        htmlBuilder.append("<p>&emsp;&emsp;" + "截止" + time + "成功</p>");
        htmlBuilder.append("<table border=\"1\" cellspacing=\"0\" cellpadding=\"0\" style=\"width:60%;margin-left:10;text-align:center\">\n");
        htmlBuilder.append("<tr>\n" +
                "<th>统计项</th>\n" +
                "<th>下载</th>\n" +
                "<th>上传</th>\n" +
                "</tr>\n");
        htmlBuilder.append("<tr>\n" +
                "<td>文件数量</td>\n" +
                "<td>" + StatisticsCommon.STATISTICS_COMMON.getDownloadLocalFileTotal() + "</td>\n" +
                "<td>" + StatisticsCommon.STATISTICS_COMMON.getUploadFtpFileTotal() + "</td>\n" +
                "</tr>\n");
        htmlBuilder.append("<tr>\n" +
                "<td>文件大小</td>\n" +
                "<td>" + changeSizeToString(StatisticsCommon.STATISTICS_COMMON.getDownloadLocalFileSize()) + "</td>\n" +
                "<td>" + changeSizeToString(StatisticsCommon.STATISTICS_COMMON.getUploadFtpFileSize()) + "</td>\n" +
                "</tr>\n");
        htmlBuilder.append("</table>\n");
        List<String> missDownloadPath = StatisticsCommon.STATISTICS_COMMON.getMissDownloadFilePath();
        List<String> missDownloadPathTemp = new ArrayList<>(missDownloadPath);
        List<String> missUploadPath = StatisticsCommon.STATISTICS_COMMON.getMissUploadFilePath();
        //获得相同路径
        missDownloadPathTemp.retainAll(missUploadPath);
        //去除相同路径，即去除下载不完整并且没有上传的文件路径
        missUploadPath.removeAll(missDownloadPathTemp);
        Integer movieLength = missDownloadPath.size() + missUploadPath.size();
        htmlBuilder.append("<p>&emsp;&emsp;" + "截止" + time + "失败文件" + (movieLength == 0 ? "：无" : "如下表：") + "</p>");
        if (movieLength != 0) {
            htmlBuilder.append("<table border=\"1\" cellspacing=\"0\" cellpadding=\"0\"  style=\"width:60%;margin-left:10;text-align:center\">\n");
            htmlBuilder.append("<tr>\n" +
                    "<th>序号</th>\n" +
                    "<th>文件路径</th>\n" +
                    "<th>下载</th>\n" +
                    "<th>上传</th>\n" +
                    "</tr>\n");
            int i = 1;
            if (missUploadPath.size() > 0) {
                for (String moviePath : missUploadPath) {
                    htmlBuilder.append("<tr>\n" +
                            "<td>" + i + "</td>\n" +
                            "<td>" + moviePath + "</td>\n" +
                            "<td>&#10003</td>\n" +
                            "<td>&#10005</td>\n" +
                            "</tr>\n");
                    i++;
                }
            }
            if (missDownloadPath.size() > 0) {
                for (String moviePath : missDownloadPath) {
                    htmlBuilder.append("<tr>\n" +
                            "<td>" + i + "</td>\n" +
                            "<td>" + moviePath + "</td>\n" +
                            "<td>&#10005</td>\n" +
                            "<td>&#10005</td>\n" +
                            "</tr>\n");
                    i++;
                }
            }
            htmlBuilder.append("</table>\n");
        }
        htmlBuilder.append("<p>&emsp;&emsp;<strong>注意事项：</strong>程序正在下载或者上传文件时，统计下载上传情况可能导致下载上传出现异常，程序会自动恢复下载或上传；同时也可能导致统计异常，统计不准确。</p>");
        try {
            sendMailService.sendHtmlMails(subject, htmlBuilder.toString(), DownloadCommon.DOWNLOAD_COMMON.getFromEmail(), DownloadCommon.DOWNLOAD_COMMON.getReceivers());
        } catch (Exception e) {
            log.error("发送邮件失败");
        }
    }

    private String changeSizeToString(Long size) {
        DecimalFormat df = new DecimalFormat("#.00");
        String sizeString = "";
        if (size < 1204) {
            sizeString = size + "B";
        }
        if (size >= 1024 && size < 1024 * 1024) {
            sizeString = df.format(size / 1024.0) + "KB";
        }
        if (size >= 1024 * 1024 && size < 1024 * 1024 * 1024) {
            sizeString = df.format(size / 1024 / 1024.0) + "MB";
        }
        if (size >= 1024 * 1024 * 1024) {
            sizeString = df.format(size / 1024 / 1024 / 1024.0) + "GB";
        }
        return sizeString;
    }
}
