package com.wangyb.learningdemo.authentication.controller.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * Created with Intellij IDEA.
 *
 * @author wangyb
 * @Date 2018/9/25 13:47
 * Modified By:
 * Description:用于接收添加角色的参数
 */
@Data
public class RoleAddReq {
    @ApiModelProperty(value = "角色名",required = true)
    private String roleName;
    @ApiModelProperty(value = "角色描述")
    private String memo;
    @ApiModelProperty(value = "权限Id列表",required = true)
    private List<Integer> permissionIds;

    public RoleAddReq(String roleName, String memo, List<Integer> permissionIds) {
        this.roleName = roleName;
        this.memo = memo;
        this.permissionIds = permissionIds;
    }
}