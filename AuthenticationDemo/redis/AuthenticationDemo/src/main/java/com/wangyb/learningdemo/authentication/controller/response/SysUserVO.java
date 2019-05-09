package com.wangyb.learningdemo.authentication.controller.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * Created with Intellij IDEA.
 *
 * @author wangyb
 * @Date 2018/10/9 8:56
 * Modified By:
 * Description:用于返回用户的详细信息，密码除外
 */
@Data
public class SysUserVO {
    @ApiModelProperty(value = "用户id")
    private Integer userId;
    @ApiModelProperty(value = "用户名")
    private String userName;
    @ApiModelProperty(value = "登录名")
    private String loginName;
    @ApiModelProperty(value = "邮箱")
    private String email;
    @ApiModelProperty(value = "用户是否有效")
    private Integer enabled;
    @ApiModelProperty(value = "组织id")
    private Integer organizationId;
    @ApiModelProperty(value = "创建时间")
    private Date createTime;
    @ApiModelProperty(value = "有效期")
    private Date modifyTime;

    public SysUserVO(Integer userId,Date createTime, Date modifyTime, String userName, String loginName, String email, Integer enabled, Integer organizationId) {
        this.userId = userId;
        this.createTime = createTime;
        this.modifyTime = modifyTime;
        this.userName = userName;
        this.loginName = loginName;
        this.email = email;
        this.enabled = enabled;
        this.organizationId = organizationId;
    }
}
