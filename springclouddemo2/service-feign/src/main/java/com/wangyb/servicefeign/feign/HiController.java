package com.wangyb.servicefeign.feign;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author wangyb
 * @Date 2019/4/24 14:30
 * Modified By:
 * Description:
 */
@RestController
@Slf4j
public class HiController {

    @Autowired
    SchedualServiceHi schedualServiceHi;

    @GetMapping(value = "/hi")
    public String sayHi(@RequestParam String name) {
        log.info("service-feign 处理请求");
        return schedualServiceHi.sayHiFromClientOne( name );
    }
}
