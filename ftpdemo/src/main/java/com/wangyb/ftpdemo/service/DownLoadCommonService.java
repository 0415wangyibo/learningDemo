package com.wangyb.ftpdemo.service;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.wangyb.ftpdemo.pojo.DownLoadPo;
import com.wangyb.ftpdemo.pojo.JobCommon;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created with Intellij IDEA.
 *
 * @author wangyb
 * @Date 2019/1/29 15:26
 * Modified By:
 * Description:
 */
@Service
@Slf4j
public class DownLoadCommonService {

    private final Gson gson;

    @Autowired
    public DownLoadCommonService(Gson gson) {
        this.gson = gson;
    }

    /**
     * 从文件中读取下载历史信息
     *
     * @throws IOException
     */
    public void readDownLoadInfoFromFile() throws IOException {
        //获取项目根路径
        File directory = new File("");// 参数为空
        String courseFile = directory.getCanonicalPath();
        String downLoadPath = courseFile + "/downLoadInfo.txt";
        File downLoadFile = new File(downLoadPath);
        if (downLoadFile.exists()) {
            List<String> downLoadList = Files.readLines(downLoadFile, Charsets.UTF_8);
            if (null != downLoadList && downLoadList.size() != 0) {
                DownLoadPo downLoadPo = gson.fromJson(downLoadList.get(0), DownLoadPo.class);
                if (null != downLoadPo && null != downLoadPo.getDayDownLoadInfoList()) {
                    JobCommon.JOB_COMMON.resetDownLoadInfo(downLoadPo.getDayDownLoadInfoList());
                }
            }
        }
    }

    /**
     * 将下载信息存入文件进行持久化
     *
     */
    public void saveDownLoadInfoToFile() {
        //获取项目根路径
        try {
            File directory = new File("");// 参数为空
            String courseFile = directory.getCanonicalPath();
            String downLoadPath = courseFile + "/downLoadInfo.txt";
            DownLoadPo downLoadPo = new DownLoadPo();
            downLoadPo.setDayDownLoadInfoList(JobCommon.JOB_COMMON.getAllDownLoadInfo());
            Files.asCharSink(new File(downLoadPath), Charsets.UTF_8).write(gson.toJson(downLoadPo));
        } catch (IOException e) {
            log.info("下载历史写入文件失败");
        }
    }
}
