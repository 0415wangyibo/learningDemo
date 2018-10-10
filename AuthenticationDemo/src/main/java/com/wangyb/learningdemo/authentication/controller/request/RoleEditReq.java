package com.wangyb.learningdemo.authentication.controller.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * Created with Intellij IDEA.
 *
 * @author wangyb
 * @Date 2018/9/25 13:54
 * Modified By:
 * Description:用于接收修改角色权限的参数
 */
@Data
public class RoleEditReq {
    @ApiModelProperty(value = "角色Id",required = true)
    private Integer roleId;
    @ApiModelProperty(value = "角色名",required = true)
    private String roleName;
    @ApiModelProperty(value = "角色描述")
    private String memo;
    @ApiModelProperty(value = "权限Id列表",required = true)
    private List<Integer> permissionIds;

    public RoleEditReq(Integer roleId, String roleName, String memo, List<Integer> permissionIds) {
        this.roleId = roleId;
        this.roleName = roleName;
        this.memo = memo;
        this.permissionIds = permissionIds;
    }
}