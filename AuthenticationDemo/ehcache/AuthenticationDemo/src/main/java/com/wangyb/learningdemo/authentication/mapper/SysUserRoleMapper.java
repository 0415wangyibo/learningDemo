package com.wangyb.learningdemo.authentication.mapper;

import com.wangyb.learningdemo.authentication.config.BaseMapper;
import com.wangyb.learningdemo.authentication.entity.SysUserRole;
import org.apache.ibatis.annotations.CacheNamespaceRef;
import org.apache.ibatis.annotations.Mapper;
/**
 * Created with Intellij IDEA.
 *
 * @author wangyb
 * @Date 2018/10/9 9:12
 * Modified By:
 * Description:
 */
@Mapper
//@CacheNamespaceRef(SysUserRoleMapper.class)
public interface SysUserRoleMapper extends BaseMapper<SysUserRole>{
    /**
     * 根据组织id删除所有的角色对应关系
     * @param organizationId
     * @return
     */
    Integer deleteUserRoleByOrganizationId(Integer organizationId);
}
