package com.wangyb.learningdemo.authentication.controller.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created with Intellij IDEA.
 *
 * @author wangyb
 * @Date 2018/9/21 10:21
 * Modified By:
 * Description:用于接收用户登录的参数
 */
@Data
public class LoginReq {
    @ApiModelProperty(value = "登录名",required = true)
    private String loginName;
    @ApiModelProperty(value = "密码",required = true)
    private String password;

    public LoginReq(String loginName, String password) {
        this.loginName = loginName;
        this.password = password;
    }
}
