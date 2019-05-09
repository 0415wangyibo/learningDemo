package com.wangyb.learningdemo.authentication.controller.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created with Intellij IDEA.
 *
 * @author wangyb
 * @Date 2018/9/25 14:03
 * Modified By:
 * Description:用于接收编辑后台用户的参数
 */
@Data
public class UserEditReq {
    @ApiModelProperty(value = "用户id",required = true)
    private Integer userId;
    @ApiModelProperty(value = "用户名",required = true)
    private String userName;
    @ApiModelProperty(value = "角色Id",required = true)
    private Integer roleId;
    @ApiModelProperty(value = "邮箱")
    private String email;

    public UserEditReq(Integer userId, String userName, Integer roleId, String email) {
        this.userId = userId;
        this.userName = userName;
        this.roleId = roleId;
        this.email = email;
    }
}
