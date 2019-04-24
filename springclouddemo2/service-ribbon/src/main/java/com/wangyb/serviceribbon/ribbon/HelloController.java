package com.wangyb.serviceribbon.ribbon;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author wangyb
 * @Date 2019/4/24 14:11
 * Modified By:
 * Description:
 */
@RestController
@Slf4j
public class HelloController {

    @Autowired
    HelloService helloService;

    @GetMapping(value = "/hi")
    public String hi(@RequestParam String name) {
        log.info("service-ribbon 处理请求");
        return helloService.hiService( name );
    }
}
