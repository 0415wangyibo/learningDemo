package com.wangyb.ftpdemo.controller;

import com.wangyb.ftpdemo.config.UploadCommon;
import com.wangyb.ftpdemo.controller.request.UploadReq;
import com.wangyb.ftpdemo.pojo.RestResult;
import com.wangyb.ftpdemo.service.UploadCommonService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

/**
 * Created with Intellij IDEA.
 *
 * @author wangyb
 * @Date 2019/2/15 16:56
 * Modified By:
 * Description: 上传配置接口
 */
@RestController
@RequestMapping("uploadCommon")
@CrossOrigin
@Api(tags = "上传配置")
public class UploadCommonController {

    private final UploadCommonService uploadCommonService;

    @Autowired
    public UploadCommonController(UploadCommonService uploadCommonService) {
        this.uploadCommonService = uploadCommonService;
    }

    @GetMapping("")
    @ApiOperation("查看上传配置信息")
    public RestResult<UploadReq> getUploadReq(){
        return new RestResult<>(new UploadReq(UploadCommon.UPLOAD_COMMON));
    }

    @PostMapping("")
    @ApiOperation("更改上传配置信息")
    public RestResult<Boolean> postUploadReq(UploadReq uploadReq) throws IOException {
        return new RestResult<>(uploadCommonService.saveUploadToFile(uploadReq));
    }
}
