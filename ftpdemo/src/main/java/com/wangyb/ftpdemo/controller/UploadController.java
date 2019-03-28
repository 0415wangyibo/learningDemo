package com.wangyb.ftpdemo.controller;

import com.wangyb.ftpdemo.controller.response.ResponseInfo;
import com.wangyb.ftpdemo.pojo.DayDownLoadInfo;
import com.wangyb.ftpdemo.pojo.RestResult;
import com.wangyb.ftpdemo.service.UploadService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Created with Intellij IDEA.
 *
 * @author wangyb
 * @Date 2019/2/16 14:52
 * Modified By:
 * Description: 上传接口
 */
@RestController
@RequestMapping("upload")
@CrossOrigin
@Api(tags = "上传接口")
public class UploadController {

    private final UploadService uploadService;

    @Autowired
    public UploadController(UploadService uploadService) {
        this.uploadService = uploadService;
    }

    @PostMapping("/job")
    @ApiOperation("手动添加对某个文件夹上传的任务")
    public RestResult<ResponseInfo> putDownLoadJob(String downName){
        return new RestResult<>(uploadService.addUploadJob(downName));
    }

    @GetMapping("")
    @ApiOperation("手动检查某个文件夹的上传情况")
    public RestResult<DayDownLoadInfo> checkUploadByDownName(String downName) {
        return new RestResult<>(uploadService.checkUploadByDownName(downName));
    }
}
