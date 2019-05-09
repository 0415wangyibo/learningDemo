package com.wangyb.learningdemo.authentication.mapper;

import com.wangyb.learningdemo.authentication.config.BaseMapper;
import com.wangyb.learningdemo.authentication.controller.response.SysUserVO;
import com.wangyb.learningdemo.authentication.entity.SysUser;
import org.apache.ibatis.annotations.CacheNamespaceRef;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * Created with Intellij IDEA.
 *
 * @author wangyb
 * @Date 2018/10/9 8:55
 * Modified By:
 * Description:
 */
@Mapper
//@CacheNamespaceRef(SysUserMapper.class)
public interface SysUserMapper extends BaseMapper<SysUser>{
    /**
     * 根据loginName获取用户
     *
     * @param loginName
     * @return
     */
    SysUser selectByLoginName(String loginName);


    /**
     * 根据组织id查询用户信息
     *
     * @param organizationId
     * @return
     */
    List<SysUserVO> selectUserVOByOrganizationId(@Param("organizationId") Integer organizationId);

    /**
     * 更新用户的有效期
     * @param modify
     * @param organizationId
     * @return
     */
    Integer updateUserModifyTimeByOrganizationId(@Param("modify") Date modify, @Param("organizationId") Integer organizationId);

    /**
     * 将整个组织的所有管理员禁用或者解除禁用
     * @param enabled
     * @param organizationId
     * @return
     */
    Integer setUserEnabledByOrganizationId(@Param("enabled") Integer enabled, @Param("organizationId") Integer organizationId);

    /**
     * 统计指定组织已经创建的管理员/用户个数
     * @param organizationId
     * @return
     */
    Integer countUserByOrganizationId(@Param("organizationId") Integer organizationId);

    /**
     * 通过userId查找用户
     * @param userId
     * @return
     */
    SysUser selectUserById(Integer userId);
}
