package com.wangyb.learningdemo.authentication.mapper;

import com.wangyb.learningdemo.authentication.config.BaseMapper;
import com.wangyb.learningdemo.authentication.controller.response.PermissionVO;
import com.wangyb.learningdemo.authentication.entity.SysPermission;
import org.apache.ibatis.annotations.CacheNamespaceRef;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * Created with Intellij IDEA.
 *
 * @author wangyb
 * @Date 2018/10/8 17:35
 * Modified By:
 * Description:
 */
@Mapper
@CacheNamespaceRef(SysPermissionMapper.class)
public interface SysPermissionMapper extends BaseMapper<SysPermission>{
    /**
     * 根据父id列表获取子权限列表
     *
     * @param list
     * @return
     */
    List<PermissionVO> selectPermissionVOByParentIds(List<Integer> list);


    /**
     * 通过角色id查找主权限
     * @param roleId
     * @return
     */
    List<PermissionVO> selectMainPermissionByRoleId(Integer roleId);

    /**
     * 通过角色Id查找权限
     * @param roleId
     * @return
     */
    List<PermissionVO> selectPermissionByRoleId(Integer roleId);

    /**
     * 查询用户角色对应的权限
     * @param userId
     * @return
     */
    List<PermissionVO> selectPermissionVOByUserId(Integer userId);
}
