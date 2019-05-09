package com.wangyb.learningdemo.authentication.entity;

import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import java.io.Serializable;
/**
 * Created with Intellij IDEA.
 *
 * @author wangyb
 * @Date 2018/10/8 17:01
 * Modified By:
 * Description:
 */
@Data
public class SysRole implements Serializable{
    private static final Long serialVersionUID = 1L;

    @Id
    @KeySql(useGeneratedKeys = true)
    private Integer id;
    private String roleName;//角色名
    private String memo;//角色描述
    private Integer organizationId;//组织id不为空则表明是该组织所建立的管理员角色，用于赋予权限使用，如果为0则是管理部门所建立的角色
    private Integer userId;//创建该权限的管理员id，如果id为0，则说明该角色是系统角色

    public SysRole(String roleName,String memo,Integer organizationId,Integer userId){
        this.roleName = roleName;
        this.memo = memo;
        this.organizationId = organizationId;
        this.userId = userId;
    }

    public SysRole(){

    }
}
