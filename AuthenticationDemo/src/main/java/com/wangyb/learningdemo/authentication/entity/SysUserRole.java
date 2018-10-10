package com.wangyb.learningdemo.authentication.entity;

import lombok.Data;
import java.io.Serializable;
/**
 * Created with Intellij IDEA.
 *
 * @author wangyb
 * @Date 2018/10/8 16:58
 * Modified By:
 * Description:
 */
@Data
public class SysUserRole implements Serializable {
    private static final Long serialVersionUID = 1L;

    private Integer userId;//用户id
    private Integer roleId;//角色id

    public SysUserRole(Integer userId, Integer roleId) {
        this.userId = userId;
        this.roleId = roleId;
    }

    public SysUserRole() {

    }
}
