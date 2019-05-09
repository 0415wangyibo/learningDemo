package com.wangyb.learningdemo.authentication.mapper;

import com.wangyb.learningdemo.authentication.config.BaseMapper;
import com.wangyb.learningdemo.authentication.entity.Organization;
import org.apache.ibatis.annotations.CacheNamespaceRef;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * Created with Intellij IDEA.
 *
 * @author wangyb
 * @Date 2018/10/8 17:25
 * Modified By:
 * Description:
 */
@Mapper
//@CacheNamespaceRef(OrganizationMapper.class)
public interface OrganizationMapper extends BaseMapper<Organization>{

    /**
     * 通过id查找组织
     * @param organizationId
     * @return
     */
    Organization selectOrganizationById(Integer organizationId);
    /**
     * 设定指定组织所能创建的最大个数
     * @param maxNumber
     * @param organizationId
     * @return
     */
    Integer setOrganizationMaxUserNumberByOrganizationId(@Param("maxNumber") Integer maxNumber, @Param("organizationId") Integer organizationId);
}
