package com.example.quartz.controller;

import com.example.quartz.RestResult;
import com.example.quartz.service.TestNameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created with Intellij IDEA.
 *
 * @author wangyb
 * @Date 2018/12/4 16:56
 * Modified By:
 * Description: 实现如何在不重新启动项目的前提下动态改变项目的配置文件
 */
@RestController
@RequestMapping("test")
public class TestNameController {

    private final TestNameService testNameService;

    @Autowired
    public TestNameController(TestNameService testNameService) {
        this.testNameService = testNameService;
    }

    @Value("${config.name}")
    private String name;

    //此方式更改配置文件后，执行maven中Plugins下的resources插件后不能动态更新name的值
    @GetMapping("")
    public RestResult<String> getName(){
        return new RestResult<>(name);
    }

    //此方式更改name.json文件中的内容后，执行maven中Plugins下的resources插件后可以动态更新name的值
    //如果name.json的路径是电脑中的某一绝对路径，即时打包成jar文件，执行后只要更改了name.json文件就可以实时更新系统中的配置，不需要额外执行操作
    @GetMapping("/name")
    public RestResult<String> getNameFromJson(){
        return new RestResult<>(testNameService.getName());
    }
}
