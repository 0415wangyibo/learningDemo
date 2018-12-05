package com.example.quartz;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Created with Intellij IDEA.
 *
 * @author wangyb
 * @Date 2018/12/4 14:01
 * Modified By:
 * Description:
 */
@Component
@Order(1)
public class MyBean implements CommandLineRunner{

    @Override
    public void run(String... args) throws Exception {
        System.out.println("初始化程序");
    }
}
