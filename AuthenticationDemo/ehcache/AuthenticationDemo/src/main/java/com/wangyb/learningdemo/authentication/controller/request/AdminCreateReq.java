package com.wangyb.learningdemo.authentication.controller.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created with Intellij IDEA.
 *
 * @author wangyb
 * @Date 2018/9/25 14:09
 * Modified By:
 * Description:用于接收创建组织初始管理员的参数
 */
@Data
public class AdminCreateReq {
    @ApiModelProperty(value = "yyyy-MM-dd HH:mm:ss 格式的时间",required = true)
    private String modifyString;
    @ApiModelProperty(value = "用户名",required = true)
    private String userName;
    @ApiModelProperty(value = "登录名",required = true)
    private String loginName;
    @ApiModelProperty(value = "邮箱")
    private String email;
    @ApiModelProperty(value = "管理员是否有效，如果不填或者非0则为有效")
    private Integer enabled;
    @ApiModelProperty(value = "组织Id",required = true)
    private Integer organizationId;

    public AdminCreateReq(String modifyString, String userName, String loginName, String email, Integer enabled, Integer organizationId) {
        this.modifyString = modifyString;
        this.userName = userName;
        this.loginName = loginName;
        this.email = email;
        this.enabled = enabled;
        this.organizationId = organizationId;
    }
}

