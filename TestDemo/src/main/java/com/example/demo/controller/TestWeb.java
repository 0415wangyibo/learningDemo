package com.example.demo.controller;

import com.example.demo.pojo.RestResult;
import io.swagger.annotations.ApiOperation;
import org.apache.http.entity.StringEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created with Intellij IDEA.
 *
 * @author wangyb
 * @Date 2019/1/7 14:08
 * Modified By:
 * Description:
 */
@RestController
@RequestMapping("test")
public class TestWeb {

    @PostMapping(path = "/name")
    @ApiOperation("测试获得名字")
    public RestResult<String> listName(String name) {
        String names = "名字：" + name;
        return new RestResult<>(names);
    }

    @PostMapping(path = "/email")
    @ApiOperation("测试获得邮箱")
    public RestResult<String> listEmail(String email) {
        String emails = "邮箱：" + email;
        return new RestResult<>(emails);
    }
}
