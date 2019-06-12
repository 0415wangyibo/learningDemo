package com.wangyibo.fulltextdemo.entity;

import com.wangyibo.fulltextdemo.util.NameUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.Date;

/**
 * @author wangyb
 * @Date 2019/6/11 16:15
 * Modified By:
 * Description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Contact implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @KeySql(useGeneratedKeys = true)
    private Integer id;
    private Date createTime;//创建时间
    private Date updateTime;//更新时间
    private String name;//姓名
    private String sex;//性别
    private String contact;//联系方式
    private String chPosition;//中文职位
    private String position;//职位
    private String positionAbbr;//职位缩写
    private String lastName;//姓
    private String firstName;//名
    private String abbrName;//名字缩写
    @Transient
    private Float relevance;

    public Contact(String name, String sex, String contact, String chPosition) {
        this.updateTime = new Date();
        this.name = name;
        this.sex = sex;
        this.contact = contact;
        this.chPosition = chPosition;
        this.position = NameUtil.nameToPinyinWithoutToneSplitWhiteSpace(chPosition) + " " + NameUtil.nameToPinyinWithToneSplitWhiteSpace(chPosition);
        this.positionAbbr = NameUtil.getAbbrFromPinyin(this.position);
        String lastName = NameUtil.getLastName(this.name);
        this.lastName = NameUtil.nameToPinyinWithoutToneSplitWhiteSpace(lastName) + " " + NameUtil.nameToPinyinWithToneSplitWhiteSpace(lastName);
        String firstName = this.name.substring(lastName.length());
        this.firstName = NameUtil.nameToPinyinWithoutToneSplitWhiteSpace(firstName) + " " + NameUtil.nameToPinyinWithToneSplitWhiteSpace(firstName);
        String abbrName = NameUtil.getAbbrFromPinyin(NameUtil.nameToPinyinWithoutToneSplitWhiteSpace(this.name));
        this.abbrName = abbrName + abbrName;
    }
}
