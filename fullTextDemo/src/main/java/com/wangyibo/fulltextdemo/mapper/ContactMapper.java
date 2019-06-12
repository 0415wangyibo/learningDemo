package com.wangyibo.fulltextdemo.mapper;

import com.wangyibo.fulltextdemo.config.BaseMapper;
import com.wangyibo.fulltextdemo.entity.Contact;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.spring.annotation.MapperScan;

import java.util.List;

/**
 * @author wangyb
 * @Date 2019/6/11 16:22
 * Modified By:
 * Description:
 */
@MapperScan
@Repository
public interface ContactMapper extends BaseMapper<Contact> {

    /**
     * 通过联系电话查找联系人
     */
    Contact selectContactsByContact(@Param("contact") String contact);

    /**
     * 根据姓名信息查找联系人
     *
     * @param lastName  姓，无音调
     * @param firstName 名，无音调
     * @param abbrName  姓名缩写
     * @param fullName  全名，无音调
     * @param toneLastName 姓，有音调
     * @param toneFirstName 名，有音调
     * @param toneFullName 姓名，有音调
     */
    List<Contact> selectContactsByCondition(@Param("lastName") String lastName, @Param("firstName") String firstName,
                                            @Param("abbrName") String abbrName, @Param("fullName") String fullName,
                                            @Param("toneLastName") String toneLastName, @Param("toneFirstName") String toneFirstName,
                                            @Param("toneFullName") String toneFullName);

    /**
     * 根据职位信息查找联系人
     * @param lastName 姓，无音调拼音
     * @param position 不含姓的职位，无音调拼音
     * @param fullPosition 职位全称，无音调拼音
     * @param positionAbbr 职位缩写，双倍
     * @param toneLastName 姓，有音调拼音
     * @param toneNewPositionPinyin 不含姓的职位，有音调拼音
     * @param toneFullPosition 职位全称，有音调拼音
     */
    List<Contact> selectContactsByPosition(@Param("lastName") String lastName, @Param("position") String position,
                                           @Param("fullPosition") String fullPosition, @Param("positionAbbr") String positionAbbr,
                                           @Param("toneLastName") String toneLastName, @Param("toneNewPositionPinyin") String toneNewPositionPinyin,
                                           @Param("toneFullPosition") String toneFullPosition);
}
