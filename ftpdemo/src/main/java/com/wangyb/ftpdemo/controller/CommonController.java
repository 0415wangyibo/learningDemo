package com.wangyb.ftpdemo.controller;

import com.wangyb.ftpdemo.config.DownloadCommon;
import com.wangyb.ftpdemo.controller.request.CommonReq;
import com.wangyb.ftpdemo.pojo.RestResult;
import com.wangyb.ftpdemo.service.CommonService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

/**
 * Created with Intellij IDEA.
 *
 * @author wangyb
 * @Date 2019/1/28 15:33
 * Modified By:
 * Description: 下载配置接口
 */
@RestController
@RequestMapping("common")
@CrossOrigin
@Api(tags = "下载相关配置")
public class CommonController {

    private final CommonService commonService;

    @Autowired
    public CommonController(CommonService commonService) {
        this.commonService = commonService;
    }

    @GetMapping("")
    @ApiOperation("查看系统的配置信息")
    public RestResult<CommonReq> getCommonReq(){
        return new RestResult<>(new CommonReq(DownloadCommon.DOWNLOAD_COMMON));
    }

    @PostMapping("")
    @ApiOperation("更改系统的配置信息")
    public RestResult<Boolean> postCommonReq(CommonReq commonReq) throws IOException{
        return new RestResult<>(commonService.saveCommonToFile(commonReq));
    }
}
