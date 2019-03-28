package com.wangyb.ftpdemo.controller;

import com.wangyb.ftpdemo.controller.response.ResponseInfo;
import com.wangyb.ftpdemo.pojo.DayDownLoadInfo;
import com.wangyb.ftpdemo.pojo.JobCommon;
import com.wangyb.ftpdemo.pojo.RestResult;
import com.wangyb.ftpdemo.service.DownLoadService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created with Intellij IDEA.
 *
 * @author wangyb
 * @Date 2019/1/30 11:47
 * Modified By:
 * Description: 下载接口
 */
@RestController
@RequestMapping("download")
@CrossOrigin
@Api(tags = "下载接口")
public class DownLoadController {

    private final DownLoadService downLoadService;

    @Autowired
    public DownLoadController(DownLoadService downLoadService) {
        this.downLoadService = downLoadService;
    }

    @GetMapping("")
    @ApiOperation("手动检查某个文件夹的下载情况")
    public RestResult<DayDownLoadInfo> checkDownLoadByDownName(String downName) {
        return new RestResult<>(downLoadService.checkDownLoadByDownName(downName));
    }

    @PostMapping("/job")
    @ApiOperation("手动添加对某个文件夹下载的任务")
    public RestResult<ResponseInfo> putDownLoadJob(String downName){
        return new RestResult<>(downLoadService.putDownLoadJob(downName));
    }


    @DeleteMapping("/job/{downName}")
    @ApiOperation("手动删除对某个文件夹下载的任务，不会删除文件")
    public RestResult<ResponseInfo> deleteDownLoadJob(@PathVariable("downName") String downName){
        ResponseInfo responseInfo = new ResponseInfo();
        if (downLoadService.removeOneDownLoadTask(downName)) {
            responseInfo.setIfOk(true);
            responseInfo.setReason("移除下载任务完成");
        }else {
            responseInfo.setIfOk(false);
            responseInfo.setReason("该文件夹正在下载或者上传，请耐心等待");
        }
        return new RestResult<>(responseInfo);
    }

    @GetMapping("/list")
    @ApiOperation("查看所有的任务信息")
    public RestResult<List<DayDownLoadInfo>> getDownLoadList(){
        return new RestResult<>(JobCommon.JOB_COMMON.getAllDownLoadInfo());
    }
}
