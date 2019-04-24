package com.wangyb.servicehi.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author wangyb
 * @Date 2019/4/24 11:55
 * Modified By:
 * Description:
 */
@RestController
public class TestController {

    @Value("${server.port}")
    String port;

    @RequestMapping("/hi")
    public String home(@RequestParam(value = "name", defaultValue = "wangyb") String name) {
        return "hi " + name + " ,i am from port:" + port;
    }
}
