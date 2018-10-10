package com.wangyb.learningdemo.authentication.controller.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created with Intellij IDEA.
 *
 * @author wangyb
 * @Date 2018/10/8 17:39
 * Modified By:
 * Description:用于返回权限及权限名
 */
@Data
public class PermissionVO {
    @ApiModelProperty(value = "权限id")
    private Integer permissionId;
    @ApiModelProperty(value = "权限名")
    private String permissionName;

    public PermissionVO(Integer permissionId, String permissionName) {
        this.permissionId = permissionId;
        this.permissionName = permissionName;
    }
}
