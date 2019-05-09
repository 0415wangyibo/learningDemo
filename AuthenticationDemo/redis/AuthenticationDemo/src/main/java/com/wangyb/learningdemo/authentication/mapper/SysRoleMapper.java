package com.wangyb.learningdemo.authentication.mapper;

import com.wangyb.learningdemo.authentication.config.BaseMapper;
import com.wangyb.learningdemo.authentication.controller.response.NameAndIdVO;
import com.wangyb.learningdemo.authentication.entity.SysRole;
import org.apache.ibatis.annotations.CacheNamespaceRef;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
/**
 * Created with Intellij IDEA.
 *
 * @author wangyb
 * @Date 2018/10/8 17:45
 * Modified By:
 * Description:
 */
@Mapper
@CacheNamespaceRef(SysRoleMapper.class)
public interface SysRoleMapper extends BaseMapper<SysRole>{
    /**
     * 通过用户id查询角色
     *
     * @param userId
     * @return
     */
    SysRole selectRoleByUserId(Integer userId);

    /**
     * 通过组织Id信息查询该组织下所设置的管理员角色,organizationId==0代表管理部门，不会列出系统角色
     *
     * @param organizationId
     * @return
     */
    List<SysRole> selectRoleByOrganizationId(@Param("organizationId") Integer organizationId);

    /**
     * 通过角色id查找角色
     *
     * @param id
     * @return
     */
    SysRole selectRoleById(Integer id);

    /**
     * 根据角色名及organizationId查找角色
     *
     * @param organizationId
     * @param roleName
     * @return
     */
    SysRole selectRoleByRoleNameAndOrganizationId(@Param("organizationId") Integer organizationId, @Param("roleName") String roleName);

    /**
     * 通过用户id查询用户角色
     *
     * @param userId
     * @return
     */
    List<String> selectRoleNameByUserId(@Param("userId") Integer userId);

    /**
     * 通过organizationId查询角色名及id
     * @param organizationId
     * @return
     */
    List<NameAndIdVO> selectRoleNameAndIdByOrganizationId(@Param("organizationId") Integer organizationId);
}
