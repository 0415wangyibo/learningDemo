package com.wangyb.ftpdemo.service;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.wangyb.ftpdemo.config.UploadCommon;
import com.wangyb.ftpdemo.controller.request.UploadReq;
import com.wangyb.ftpdemo.utils.PathUtil;
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
 * @Date 2019/2/15 16:23
 * Modified By:
 * Description: 上传文件service
 */
@Service
@Slf4j
public class UploadCommonService {

    private final Gson gson;

    @Autowired
    public UploadCommonService(Gson gson) {
        this.gson = gson;
    }

    //初始化Common
    public void initUploadCommon() throws IOException {
        //获取项目根路径
        File directory = new File("");// 参数为空
        String courseFile = directory.getCanonicalPath();
        String uploadPath = courseFile + "/uploadCommon.txt";
        File uploadFile = new File(uploadPath);
        UploadReq uploadReq = new UploadReq(UploadCommon.UPLOAD_COMMON);
        //如果本地配置文件存在，则导入本地配置文件
        if (uploadFile.exists()) {
            List<String> uploadList = Files.readLines(uploadFile, Charsets.UTF_8);
            if (null != uploadList && uploadList.size() != 0) {
                this.saveUpload(gson.fromJson(uploadList.get(0), UploadReq.class));
                return;
            }
        }
        this.saveUploadToFile(uploadReq);
    }

    /**
     * 将上传配置信息从uploadReq中转移到UploadCommon中，方便使用
     * @param uploadReq uploadReq
     */
    private void saveUpload(UploadReq uploadReq) {
       UploadCommon.UPLOAD_COMMON.setFtpHost(uploadReq.getFtpHost());
       UploadCommon.UPLOAD_COMMON.setFtpPort(uploadReq.getFtpPort());
       UploadCommon.UPLOAD_COMMON.setFtpUserName(uploadReq.getFtpUserName());
       UploadCommon.UPLOAD_COMMON.setFtpPassword(uploadReq.getFtpPassword());
       UploadCommon.UPLOAD_COMMON.setFtpPath(uploadReq.getFtpPath());
       UploadCommon.UPLOAD_COMMON.setIfUpload(uploadReq.getIfUpload());
    }

    /**
     * 将上传配置信息存入文件中，进行持久化，防止丢失
     * @param uploadReq uploadReq
     * @return
     * @throws IOException
     */
    public Boolean saveUploadToFile(UploadReq uploadReq) throws IOException {
        //自动添加“/”，防止程序出错
        uploadReq.setFtpPath(PathUtil.formFtpPath(uploadReq.getFtpPath()));
        //获取项目根路径
        File directory = new File("");// 参数为空
        String courseFile = directory.getCanonicalPath();
        String uploadPath = courseFile + "/uploadCommon.txt";
        Files.asCharSink(new File(uploadPath), Charsets.UTF_8).write(gson.toJson(uploadReq));
        this.saveUpload(uploadReq);
        return true;
    }
}
