package com.wangyb.learningdemo.authentication.entity;

import lombok.Data;
import java.io.Serializable;
/**
 * Created with Intellij IDEA.
 *
 * @author wangyb
 * @Date 2018/10/8 17:00
 * Modified By:
 * Description:
 */
@Data
public class SysRolePermission implements Serializable{
    public static final Long serialVersionUID = 1L;

    private Integer roleId;//角色id
    private Integer permissionId;//权限id

    public SysRolePermission(Integer roleId, Integer permissionId) {
        this.roleId = roleId;
        this.permissionId = permissionId;
    }

    public SysRolePermission(){

    }
}