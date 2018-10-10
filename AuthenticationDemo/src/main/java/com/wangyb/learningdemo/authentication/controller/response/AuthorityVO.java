package com.wangyb.learningdemo.authentication.controller.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * Created with Intellij IDEA.
 *
 * @author wangyb
 * @Date 2018/9/6 16:44
 * Modified By:
 * Description:用于返回角色及该角色对应的权限列表
 */
@Data
public class AuthorityVO {
    @ApiModelProperty(value = "角色id")
    private Integer roleId;
    @ApiModelProperty(value = "角色名称")
    private String roleName;
    @ApiModelProperty(value = "角色所拥有的权限列表")
    private List<PermissionVO> permissionVOList;

    public AuthorityVO(Integer roleId,String roleName, List<PermissionVO> permissionVOList) {
        this.roleId = roleId;
        this.roleName = roleName;
        this.permissionVOList = permissionVOList;
    }
}
