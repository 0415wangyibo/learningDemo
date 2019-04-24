package com.wangyb.servicefeign.feign;

import org.springframework.stereotype.Component;

/**
 * @author wangyb
 * @Date 2019/4/24 14:47
 * Modified By:
 * Description:
 */
@Component
public class SchedualServiceHiHystric implements SchedualServiceHi {
    @Override
    public String sayHiFromClientOne(String name) {
        return "sorry " + name;
    }
}
