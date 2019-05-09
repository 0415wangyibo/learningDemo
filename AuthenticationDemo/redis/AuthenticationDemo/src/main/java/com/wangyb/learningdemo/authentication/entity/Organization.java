package com.wangyb.learningdemo.authentication.entity;

import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import java.io.Serializable;
/**
 * Created with Intellij IDEA.
 *
 * @author wangyb
 * @Date 2018/10/8 17:07
 * Modified By:
 * Description:
 */
@Data
public class Organization implements Serializable{
    private static final long serialVersionUID = 1L;

    @Id
    @KeySql(useGeneratedKeys = true)
    private Integer id;
    private String organizationName;//组织名
    private Integer maxNumber;//该组织所能创建管理员的最大数量

    public Organization(String organizationName,Integer maxNumber) {
        this.organizationName = organizationName;
        this.maxNumber = maxNumber;
    }
}
