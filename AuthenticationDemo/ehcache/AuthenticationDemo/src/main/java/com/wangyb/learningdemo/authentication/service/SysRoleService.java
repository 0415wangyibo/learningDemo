package com.wangyb.learningdemo.authentication.service;

import com.wangyb.learningdemo.authentication.controller.response.NameAndIdVO;
import com.wangyb.learningdemo.authentication.entity.SysRole;
import com.wangyb.learningdemo.authentication.entity.SysRolePermission;
import com.wangyb.learningdemo.authentication.entity.SysUserRole;
import com.wangyb.learningdemo.authentication.exception.RequestParamErrorException;
import com.wangyb.learningdemo.authentication.mapper.SysRoleMapper;
import com.wangyb.learningdemo.authentication.mapper.SysRolePermissionMapper;
import com.wangyb.learningdemo.authentication.mapper.SysUserRoleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
/**
 * Created with Intellij IDEA.
 *
 * @author wangyb
 * @Date 2018/10/9 9:24
 * Modified By:
 * Description:角色管理
 */
@Service
@Transactional
public class SysRoleService {

    private SysRoleMapper sysRoleMapper;

    private SysRolePermissionMapper sysRolePermissionMapper;

    private SysUserRoleMapper sysUserRoleMapper;

    @Autowired
    public SysRoleService(SysRoleMapper sysRoleMapper, SysRolePermissionMapper sysRolePermissionMapper,
                          SysUserRoleMapper sysUserRoleMapper) {
        this.sysRoleMapper = sysRoleMapper;
        this.sysRolePermissionMapper = sysRolePermissionMapper;
        this.sysUserRoleMapper = sysUserRoleMapper;
    }

    /**
     * 添加角色以及对应的权限,该权限必须是添加该角色的管理员所拥有的
     *
     * @param organizationId
     * @param roleName
     * @param memo
     * @param permissionIds
     * @return
     */
    public Boolean addRole(Integer organizationId, Integer adminId, String roleName,String memo, List<Integer> permissionIds) throws Exception {
        SysRole sysRoleOld = sysRoleMapper.selectRoleByRoleNameAndOrganizationId(organizationId, roleName);
        //添加的角色名不能和系统角色名相同
        SysRole sysRoleOne = sysRoleMapper.selectRoleById(1);
        SysRole sysRoleTwo = sysRoleMapper.selectRoleById(2);
        if (organizationId == 0 && roleName.equals(sysRoleOne.getRoleName())) {
            throw new RequestParamErrorException("不能和系统角色同名");
        }
        if (organizationId != 0 && roleName.equals(sysRoleTwo.getRoleName())) {
            throw new RequestParamErrorException("不能和系统角色同名");
        }
        //获得该用户角色所对应的权限
        List<Integer> permissionIdList = sysRolePermissionMapper.selectPermissionIdsByUserId(adminId);
        //如果已经存在该角色，则不能添加，否则可以添加
        if (null == sysRoleOld) {
            SysRole sysRole = new SysRole(roleName, memo, organizationId, adminId);
            sysRoleMapper.insert(sysRole);
            //获得id
            Integer id = sysRoleMapper.selectByPrimaryKey(sysRole).getId();
            for (Integer permissionId : permissionIds) {
                //如果该管理员有对应的权限则进行授权
                if (permissionIdList.contains(permissionId)) {
                    SysRolePermission sysRolePermission = new SysRolePermission(id, permissionId);
                    sysRolePermissionMapper.insert(sysRolePermission);
                }
            }
        } else {
            throw new RequestParamErrorException("该角色已经存在，不能添加");
        }
        return true;
    }


    /**
     * 更改已经存在的角色名及权限
     *
     * @param organizationId
     * @param adminId
     * @param roleId
     * @param roleName
     * @param memo
     * @param permissionIds
     * @return
     * @throws Exception
     */
    public Boolean changeRoleByRoleIdAndPermissionIds(Integer organizationId, Integer adminId, Integer roleId, String roleName, String memo,List<Integer> permissionIds) throws Exception {
        SysRole sysRole = sysRoleMapper.selectRoleById(roleId);
        if (null == sysRole) {
            throw new RequestParamErrorException("该角色不存在");
        }
        if (!sysRole.getOrganizationId().equals(organizationId)) {
            throw new RequestParamErrorException("该角色不是您公司所创建，不能修改");
        }
        if (sysRole.getUserId() == 0) {
            throw new RequestParamErrorException("系统管理员的角色不能修改");
        }
        SysRole sysRoleOne = sysRoleMapper.selectRoleById(1);
        SysRole sysRoleTwo = sysRoleMapper.selectRoleById(2);
        if (organizationId == 0 && roleName.equals(sysRoleOne.getRoleName())) {
            throw new RequestParamErrorException("不能和系统角色同名");
        }
        if (organizationId != 0 && roleName.equals(sysRoleTwo.getRoleName())) {
            throw new RequestParamErrorException("不能和系统角色同名");
        }
        List<SysRole> sysRoleList = sysRoleMapper.selectRoleByOrganizationId(organizationId);
        for (SysRole sysRoleExist : sysRoleList) {
            if (sysRoleExist.getRoleName().equals(roleName) && !sysRoleExist.getId().equals(roleId)) {
                throw new RequestParamErrorException("该角色名已经存在");
            }
        }
        SysRole sysRoleOld = sysRoleMapper.selectRoleByUserId(adminId);
        if (null==sysRoleOld){
            throw new RequestParamErrorException("该角色被其它管理员删除");
        }else {
            if (sysRoleOld.getId().equals(roleId)){
                throw new RequestParamErrorException("不能修改自己所在的角色");
            }
        }
        //获得该用户权限
        List<Integer> permissionIdList = sysRolePermissionMapper.selectPermissionIdsByUserId(adminId);
        //获得原角色的权限id
        List<Integer> rolePermissionIds = sysRolePermissionMapper.selectPermissionIdsByRoleId(roleId);
        if (!permissionIdList.containsAll(rolePermissionIds)) {
            throw new RequestParamErrorException("您的权限比原角色小，不能修改该角色");
        }
        sysRole.setRoleName(roleName);
        sysRole.setMemo(memo);
        //删除原有的权限
        Example example = new Example(SysRolePermission.class);
        example.createCriteria().andEqualTo("roleId", roleId);
        sysRolePermissionMapper.deleteByExample(example);
        //增加新的权限
        for (Integer permissionId : permissionIds) {
            if (permissionIdList.contains(permissionId)) {
                SysRolePermission sysRolePermission = new SysRolePermission(roleId, permissionId);
                sysRolePermissionMapper.insert(sysRolePermission);
            }
        }
        sysRoleMapper.updateByPrimaryKey(sysRole);
        return true;
    }

    /**
     * 删除同一组织创建的角色，同时清除之前所有拥有该角色的用户的角色对应关系,系统角色的userId==0
     *
     * @param organizationId
     * @param adminId
     * @param roleId
     * @return
     */
    public Boolean deleteRole(Integer organizationId, Integer adminId, Integer roleId) throws Exception {
        SysRole sysRole = sysRoleMapper.selectRoleById(roleId);
        if (null == sysRole) {
            throw new RequestParamErrorException("您没有创建该角色");
        }
        if (sysRole.getUserId() == 0) {
            throw new RequestParamErrorException("系统角色不能删除：" + sysRole.getRoleName());
        }
        if (!sysRole.getOrganizationId().equals(organizationId)) {
            throw new RequestParamErrorException("该角色非您所在公司创建，不能删除");
        }
        SysRole sysRoleOld = sysRoleMapper.selectRoleByUserId(adminId);
        if (null==sysRoleOld){
            throw new RequestParamErrorException("该角色被其它管理员删除");
        }else {
            if (sysRoleOld.getId().equals(roleId)){
                throw new RequestParamErrorException("不能删除自己所在的角色");
            }
        }
        //获得该用户权限
        List<Integer> permissionIdList = sysRolePermissionMapper.selectPermissionIdsByUserId(adminId);
        //获得原角色的权限id
        List<Integer> rolePermissionIds = sysRolePermissionMapper.selectPermissionIdsByRoleId(roleId);
        if (!permissionIdList.containsAll(rolePermissionIds)) {
            throw new RequestParamErrorException("您的权限比原角色小，不能删除该角色");
        }
        sysRoleMapper.delete(sysRole);
        Example example = new Example(SysUserRole.class);
        example.createCriteria().andEqualTo("roleId", roleId);
        //删除用户与角色关系
        sysUserRoleMapper.deleteByExample(example);
        Example exampleOne = new Example(SysRolePermission.class);
        exampleOne.createCriteria().andEqualTo("roleId", roleId);
        //删除角色与权限关系
        sysRolePermissionMapper.deleteByExample(exampleOne);
        return true;
    }

    /**
     * 查看同一组织下的管理员角色及id，组织与管理部门均可用，将系统管理员角色也会添加进去
     *
     * @param organizationId
     * @return
     * @throws Exception
     */
    public List<NameAndIdVO> selectRoleNameAndIdByOrganizationId(Integer organizationId) throws Exception {
        List<NameAndIdVO> nameAndIdVOList = sysRoleMapper.selectRoleNameAndIdByOrganizationId(organizationId);
        if (null == organizationId || organizationId == 0) {
            SysRole sysRole = sysRoleMapper.selectRoleById(1);
            nameAndIdVOList.add(new NameAndIdVO(sysRole.getId(), sysRole.getRoleName()));
        } else {
            SysRole sysRole = sysRoleMapper.selectRoleById(2);
            nameAndIdVOList.add(new NameAndIdVO(sysRole.getId(), sysRole.getRoleName()));
        }
        return nameAndIdVOList;
    }
}
