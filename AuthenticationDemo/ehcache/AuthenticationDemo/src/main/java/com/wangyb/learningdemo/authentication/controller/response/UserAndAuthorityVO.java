package com.wangyb.learningdemo.authentication.controller.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**
 * Created with Intellij IDEA.
 *
 * @author wangyb
 * @Date 2018/9/11 14:51
 * Modified By:
 * Description:用于返回用户信息及对应的主权限
 */
@Data
public class UserAndAuthorityVO {
    @ApiModelProperty(value = "用户id")
    private Integer userId;
    @ApiModelProperty(value = "用户名")
    private String userName;
    @ApiModelProperty(value = "登录名")
    private String loginName;
    @ApiModelProperty(value = "角色Id")
    private Integer roleId;
    @ApiModelProperty(value = "用户角色")
    private String roleName;
    @ApiModelProperty(value = "邮箱")
    private String email;
    @ApiModelProperty(value = "主权限名列表")
    private String mainPermissionNames;

    public UserAndAuthorityVO(Integer userId,String userName, String loginName,Integer roleId, String roleName,String email, String mainPermissionNames) {
        this.userId = userId;
        this.userName = userName;
        this.loginName = loginName;
        this.roleId=roleId;
        this.roleName = roleName;
        this.email = email;
        this.mainPermissionNames = mainPermissionNames;
    }
}
