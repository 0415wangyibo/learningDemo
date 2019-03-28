package com.wangyb.ftpdemo.service;

import com.wangyb.ftpdemo.utils.PathUtil;
import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.wangyb.ftpdemo.config.DownloadCommon;
import com.wangyb.ftpdemo.controller.request.CommonReq;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created with Intellij IDEA.
 *
 * @author wangyb
 * @Date 2019/1/28 15:16
 * Modified By:
 * Description:
 */
@Service
@Slf4j
public class CommonService {

    private final Gson gson;

    @Autowired
    public CommonService(Gson gson) {
        this.gson = gson;
    }

    //初始化Common
    public void initCommon() throws IOException {
        //获取项目根路径
        File directory = new File("");// 参数为空
        String courseFile = directory.getCanonicalPath();
        String commonPath = courseFile + "/common.txt";
        File commonFile = new File(commonPath);
        CommonReq commonReq = new CommonReq(DownloadCommon.DOWNLOAD_COMMON);
        if (commonFile.exists()) {
            List<String> commonList = Files.readLines(commonFile, Charsets.UTF_8);
            if (null != commonList && commonList.size() != 0) {
                this.saveCommon(gson.fromJson(commonList.get(0), CommonReq.class));
                return;
            }
        }
        this.saveCommonToFile(commonReq);
    }

    private void saveCommon(CommonReq commonReq) {
        DownloadCommon.DOWNLOAD_COMMON.setFtpHost(commonReq.getFtpHost());
        DownloadCommon.DOWNLOAD_COMMON.setFtpPort(commonReq.getFtpPort());
        DownloadCommon.DOWNLOAD_COMMON.setFtpUserName(commonReq.getFtpUserName());
        DownloadCommon.DOWNLOAD_COMMON.setFtpPassword(commonReq.getFtpPassword());
        DownloadCommon.DOWNLOAD_COMMON.setFtpPath(commonReq.getFtpPath());
        DownloadCommon.DOWNLOAD_COMMON.setDestinationPath(commonReq.getDestinationPath());
        DownloadCommon.DOWNLOAD_COMMON.setEmailName(commonReq.getEmailName());
        //可以配置多个邮箱，用;隔开，兼容中文"；"，但不能混用
        if (StringUtils.isEmpty(commonReq.getReceivers())) {
            log.info("邮箱为空");
            DownloadCommon.DOWNLOAD_COMMON.setReceivers(null);
        } else {
            if (commonReq.getReceivers().contains(";")) {
                DownloadCommon.DOWNLOAD_COMMON.setReceivers(commonReq.getReceivers().split(";"));
            } else {
                if (commonReq.getReceivers().contains("；")) {
                    DownloadCommon.DOWNLOAD_COMMON.setReceivers(commonReq.getReceivers().split("；"));
                } else {
                    String[] broad = new String[]{commonReq.getReceivers()};
                    DownloadCommon.DOWNLOAD_COMMON.setReceivers(broad);
                }
            }
        }
        DownloadCommon.DOWNLOAD_COMMON.setDownDate(commonReq.getDownDate());
        DownloadCommon.DOWNLOAD_COMMON.setIfListen(commonReq.getIfListen());
    }

    public Boolean saveCommonToFile(CommonReq commonReq) throws IOException {
        //自动添加“/”，防止程序出错
        commonReq.setFtpPath(PathUtil.formFtpPath(commonReq.getFtpPath()));
        //获取项目根路径
        File directory = new File("");// 参数为空
        String courseFile = directory.getCanonicalPath();
        String commonPath = courseFile + "/common.txt";
        Files.asCharSink(new File(commonPath), Charsets.UTF_8).write(gson.toJson(commonReq));
        this.saveCommon(commonReq);
        return true;
    }
}
