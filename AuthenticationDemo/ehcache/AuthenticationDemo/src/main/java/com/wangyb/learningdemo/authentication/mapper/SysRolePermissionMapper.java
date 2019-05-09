package com.wangyb.learningdemo.authentication.mapper;

import com.wangyb.learningdemo.authentication.config.BaseMapper;
import com.wangyb.learningdemo.authentication.entity.SysRolePermission;
import org.apache.ibatis.annotations.CacheNamespaceRef;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
/**
 * Created with Intellij IDEA.
 *
 * @author wangyb
 * @Date 2018/10/9 8:51
 * Modified By:
 * Description:
 */
@Mapper
//@CacheNamespaceRef(SysRolePermissionMapper.class)
public interface SysRolePermissionMapper extends BaseMapper<SysRolePermission>{
    /**
     * 通过角色Id查找权限Id列表
     * @param roleId
     * @return
     */
    List<Integer> selectPermissionIdsByRoleId(Integer roleId);

    /**
     * 通过用户Id查找权限的id列表（角色所对应的id列表）
     * @param userId
     * @return
     */
    List<Integer> selectPermissionIdsByUserId(Integer userId);
}
