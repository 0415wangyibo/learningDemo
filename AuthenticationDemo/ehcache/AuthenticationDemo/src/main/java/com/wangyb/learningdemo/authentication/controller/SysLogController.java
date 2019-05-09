package com.wangyb.learningdemo.authentication.controller;

import com.github.pagehelper.PageInfo;
import com.wangyb.learningdemo.authentication.annotation.SysLog;
import com.wangyb.learningdemo.authentication.entity.SysLogInfo;
import com.wangyb.learningdemo.authentication.pojo.PageResult;
import com.wangyb.learningdemo.authentication.pojo.RestResult;
import com.wangyb.learningdemo.authentication.service.SysLogInfoService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author wangyb
 * @Date 2019/5/6 15:20
 * Modified By:
 * Description:
 */
@Api(tags = "系统日志")
@RestController
@CrossOrigin
@RequestMapping(value = "log")
public class SysLogController {

    private final SysLogInfoService sysLogInfoService;

    @Autowired
    public SysLogController(SysLogInfoService sysLogInfoService) {
        this.sysLogInfoService = sysLogInfoService;
    }

    //暂时未设置日志权限
    @SysLog(operationType = "查看",operationName = "获取日志列表")
    @GetMapping("list")
    public RestResult<PageResult<SysLogInfo>> getLogList(int pageNum, int pageSize) {
        PageInfo<SysLogInfo> info = sysLogInfoService.listLog(pageNum, pageSize);
        return new RestResult<>(new PageResult<>(info.getList(), pageNum, info.getSize(), info.getTotal()));
    }
}
