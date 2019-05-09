package com.wangyb.learningdemo.authentication.entity;

import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;
/**
 * Created with Intellij IDEA.
 *
 * @author wangyb
 * @Date 2018/10/8 16:51
 * Modified By:
 * Description:
 */
@Data
public class SysUser implements Serializable {
    private static final Long serialVersionUID = 1L;

    @Id
    @KeySql(useGeneratedKeys = true)
    private Integer id;//用户id
    private Date createTime;//用户创建时间
    private Date modifyTime;//该用户的有效期
    private String userName;//用户名
    private String loginName;//登录名
    private String userPassword;//密码
    private String email;//电子邮箱
    private Integer enabled;//是否有效，用于对管理员的禁用与解禁
    private Integer organizationId;//组织id，标记该用户属于哪个组织，组织id为0是管理部门，可以管理其他组织的管理员
    private Integer tokenVersion;//该用户登录时对应token的版本号，只有版本号符合才能登录成功

    public SysUser(Date createTime, Date modifyTime, String userName, String loginName, String userPassword, String email, Integer enabled, Integer organizationId,Integer tokenVersion) {
        this.createTime = createTime;
        this.modifyTime = modifyTime;
        this.userName = userName;
        this.loginName = loginName;
        this.userPassword = userPassword;
        this.email = email;
        this.enabled = enabled;
        this.organizationId = organizationId;
        this.tokenVersion = tokenVersion;
    }

    public SysUser(){

    }
}
