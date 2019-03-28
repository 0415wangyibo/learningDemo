package com.wangyb.ftpdemo.controller;

import io.swagger.annotations.Api;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Created with Intellij IDEA.
 *
 * @author wangyb
 * @Date 2019/2/1 16:46
 * Modified By:
 * Description:  页面路由
 */
@Controller
@Api(tags = "页面路由，不用管")
public class IndexController {

    @GetMapping("")
    public String index() {
        return "jobList.html";
    }

    @GetMapping("/download/config")
    public String downLoadConfig() {
        return "downloadConfig.html";
    }

    @GetMapping("/upload/config")
    public String uploadConfig() {
        return "uploadConfig.html";
    }

    @GetMapping("/job/list")
    public String jobList() {
        return "jobList.html";
    }

    @GetMapping("/download/admin")
    public String downLoadAdmin() {
        return "downloadAdmin.html";
    }

    @GetMapping("/upload/admin")
    public String uploadAdmin() {
        return "uploadAdmin.html";
    }
}
