package com.wangyb.mylogdemo.controller;

import com.wangyb.mylogdemo.aspect.MyLog;
import com.wangyb.mylogdemo.pojo.RestResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created with Intellij IDEA.
 *
 * @author wangyb
 * @Date 2019/3/28 12:02
 * Modified By:
 * Description:
 */
@RestController
@RequestMapping("test")
@Slf4j
public class TestController {

    @GetMapping(value = "myLog")
    @MyLog("测试MyLog")
    public RestResult<String> testMyLog() {
        return new RestResult<>("testMyLog");
    }
}
