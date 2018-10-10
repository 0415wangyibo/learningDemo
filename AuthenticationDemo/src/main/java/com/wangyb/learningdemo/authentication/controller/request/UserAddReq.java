package com.wangyb.learningdemo.authentication.controller.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created with Intellij IDEA.
 *
 * @author wangyb
 * @Date 2018/9/25 12:10
 * Modified By:
 * Description:用于接收添加后台用户的参数
 */
@Data
public class UserAddReq {
    @ApiModelProperty(value = "用户名",required = true)
    private String userName;
    @ApiModelProperty(value = "登录名",required = true)
    private String loginName;
    @ApiModelProperty(value = "角色Id",required = true)
    private Integer roleId;
    @ApiModelProperty(value = "邮箱")
    private String email;

    public UserAddReq(String userName, String loginName, Integer roleId, String email) {
        this.userName = userName;
        this.loginName = loginName;
        this.roleId = roleId;
        this.email = email;
    }
}

