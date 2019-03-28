package com.wangyb.ftpdemo.task;

import com.wangyb.ftpdemo.config.DownloadCommon;
import com.wangyb.ftpdemo.config.MyConfig;
import com.wangyb.ftpdemo.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Created with Intellij IDEA.
 *
 * @author wangyb
 * @Date 2019/1/29 9:36
 * Modified By:
 * Description: 程序启动初始化任务
 */
@Component
@Order(1)
@Slf4j
public class InitSpringBootTask implements CommandLineRunner {

    private final CommonService commonService;

    private final UploadCommonService uploadCommonService;

    private final DownLoadCommonService downLoadCommonService;

    private final DownLoadService downLoadService;

    private final UploadService uploadService;

    private final MyConfig myConfig;

    @Autowired
    public InitSpringBootTask(CommonService commonService, UploadCommonService uploadCommonService,
                              DownLoadCommonService downLoadCommonService, DownLoadService downLoadService,
                              UploadService uploadService, MyConfig myConfig) {
        this.commonService = commonService;
        this.uploadCommonService = uploadCommonService;
        this.downLoadCommonService = downLoadCommonService;
        this.downLoadService = downLoadService;
        this.uploadService = uploadService;
        this.myConfig = myConfig;
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("启动时检查配置文件，如果没有则用默认值生成配置文件");
        DownloadCommon.DOWNLOAD_COMMON.setFromEmail(myConfig.getEmail());
        commonService.initCommon();
        uploadCommonService.initUploadCommon();
        log.info("检查配置文件完成");
        log.info("检查是否有历史任务信息，如果有则读取");
        downLoadCommonService.readDownLoadInfoFromFile();
        log.info("检查历史任务信息完成");
        log.info("如果有未下载完成的任务交由定时任务去执行下载");
        downLoadService.checkAndDownLoadFile();
        log.info("检查下载任务完成");
        log.info("开始检查上传任务，如果有未上传的任务则交由定时任务上传");
        uploadService.checkAndUploadFile();
        log.info("检查上传任务完成");
    }
}
