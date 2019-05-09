package com.wangyb.learningdemo.authentication.entity;

import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import java.io.Serializable;
/**
 * Created with Intellij IDEA.
 *
 * @author wangyb
 * @Date 2018/10/8 17:05
 * Modified By:
 * Description:
 */
@Data
public class SysPermission implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @KeySql(useGeneratedKeys = true)
    private Integer id;//权限id
    private String permissionName;//权限名
    private String permissionUrl;//对应的url地址，该字段暂时无用
    private Integer parentId;//权限的父节点id，如果为0则表明它没有父节点

    public SysPermission(String permissionName, String permissionUrl,Integer parentId) {
        this.permissionName=permissionName;
        this.permissionUrl = permissionUrl;
        this.parentId = parentId;
    }

    public SysPermission(){

    }
}
