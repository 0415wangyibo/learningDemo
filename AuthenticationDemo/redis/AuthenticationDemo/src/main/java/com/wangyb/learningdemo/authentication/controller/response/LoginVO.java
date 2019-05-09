package com.wangyb.learningdemo.authentication.controller.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * Created with Intellij IDEA.
 *
 * @author wangyb
 * @Date 2018/9/12 15:53
 * Modified By:
 * Description:登录成功后返回用户基本信息及token和权限列表
 */
@Data
public class LoginVO {
    @ApiModelProperty(value = "用户登录成功后使用的token信息，每次访问要将它带上")
    private String token;
    @ApiModelProperty(value = "用户id")
    private Integer userId;
    @ApiModelProperty(value = "用户名")
    private String userName;
    @ApiModelProperty(value = "该用户所拥有的权限信息")
    private List<PermissionVO> permissionVOList;

    public LoginVO(String token, Integer userId, String userName, List<PermissionVO> permissionVOList) {
        this.token = token;
        this.userId = userId;
        this.userName = userName;
        this.permissionVOList = permissionVOList;
    }
}
