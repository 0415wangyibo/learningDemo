package com.wangyb.learningdemo.authentication.service;

import com.wangyb.learningdemo.authentication.controller.response.*;
import com.wangyb.learningdemo.authentication.entity.*;
import com.wangyb.learningdemo.authentication.exception.RequestParamErrorException;
import com.wangyb.learningdemo.authentication.mapper.*;
import com.wangyb.learningdemo.authentication.utils.JwtUtil;
import com.wangyb.learningdemo.authentication.utils.TimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
/**
 * Created with Intellij IDEA.
 *
 * @author wangyb
 * @Date 2018/10/9 9:48
 * Modified By:
 * Description:
 */

@Service
@Transactional
public class SysUserService {

    @Value("${wangyb.default-password}")
    private String defaultPassword;

    private SysUserMapper sysUserMapper;
    private SysRoleMapper sysRoleMapper;
    private SysRolePermissionMapper sysRolePermissionMapper;
    private SysPermissionMapper sysPermissionMapper;
    private SysUserRoleMapper sysUserRoleMapper;
    private MyPasswordService myPasswordService;
    private OrganizationMapper organizationMapper;

    @Autowired
    public SysUserService(SysUserMapper sysUserMapper, SysRoleMapper sysRoleMapper, SysPermissionMapper sysPermissionMapper, SysUserRoleMapper sysUserRoleMapper,
                          MyPasswordService myPasswordService, SysRolePermissionMapper sysRolePermissionMapper, OrganizationMapper organizationMapper) {
        this.sysUserMapper = sysUserMapper;
        this.sysRoleMapper = sysRoleMapper;
        this.sysRolePermissionMapper = sysRolePermissionMapper;
        this.sysPermissionMapper = sysPermissionMapper;
        this.sysUserRoleMapper = sysUserRoleMapper;
        this.myPasswordService = myPasswordService;
        this.organizationMapper = organizationMapper;
    }

    /**
     * 根据用户登录名查找 user
     *
     * @param loginName
     * @return boUser
     */
    @Transactional(readOnly = true)
    public SysUser getUser(String loginName) {
        return sysUserMapper.selectByLoginName(loginName);
    }

    /**
     * 组织登录，如果登录成功则返回对应的token
     *
     * @param loginName
     * @param password
     * @return
     */
    @Transactional(readOnly = true)
    public LoginVO organizationLogin(String loginName, String password) throws Exception {
        SysUser sysUser = getUser(loginName);
        if (sysUser == null) {
            throw new RequestParamErrorException("用户不存在");
        }
        if (sysUser.getOrganizationId() == 0) {
            throw new RequestParamErrorException("此处为组织登录入口，admin不能登录");
        }
        String string = loginName + password;
        if (!myPasswordService.passwordsMatch(string, sysUser.getUserPassword())) {
            throw new RequestParamErrorException("密码错误");
        }
        Integer tokenVersion = sysUser.getTokenVersion();
        //如果token的版本号为null或者已经达到int的最大值，则重新赋值为0
        if (null == tokenVersion||tokenVersion.equals(Integer.MAX_VALUE)) {
            tokenVersion = 0;
        }
        tokenVersion++;
        try {
            //更新token版本，使之前的token失效
            String token = JwtUtil.sign(loginName, sysUser.getId(), sysUser.getOrganizationId(), tokenVersion, sysUser.getUserPassword());
            sysUser.setTokenVersion(tokenVersion);
            sysUserMapper.updateByPrimaryKey(sysUser);
            List<PermissionVO> list = selectPermissionVOByUseId(sysUser.getId());
            return new LoginVO(token, sysUser.getId(), sysUser.getUserName(), list);
        } catch (Exception e) {
            throw new Exception("登录异常");
        }
    }

    @Transactional(readOnly = true)
    public LoginVO adminLogin(String loginName, String password) throws Exception {
        System.out.println(loginName);
        SysUser sysUser = getUser(loginName);
        if (sysUser == null) {
            throw new RequestParamErrorException("用户不存在");
        }
        if (sysUser.getOrganizationId() != 0) {
            throw new RequestParamErrorException("此处为admin登录入口，组织不能登录");
        }
        String string = loginName + password;
        if (!myPasswordService.passwordsMatch(string, sysUser.getUserPassword())) {
            throw new RequestParamErrorException("密码错误");
        }
        Integer tokenVersion = sysUser.getTokenVersion();
        //如果token的版本号为null或者已经达到int的最大值，则重新赋值为0
        if (null == tokenVersion||tokenVersion.equals(Integer.MAX_VALUE)) {
            tokenVersion = 0;
        }
        tokenVersion++;
        try {
            //更新token版本，使之前的token失效
            String token = JwtUtil.sign(loginName, sysUser.getId(), sysUser.getOrganizationId(), tokenVersion, sysUser.getUserPassword());
            sysUser.setTokenVersion(tokenVersion);
            sysUserMapper.updateByPrimaryKey(sysUser);
            List<PermissionVO> list = selectPermissionVOByUseId(sysUser.getId());
            return new LoginVO(token, sysUser.getId(), sysUser.getUserName(), list);
        } catch (Exception e) {
            throw new Exception("登录异常");
        }
    }

    /**
     * 通过userId获得角色名列表,用于shiro添加角色
     *
     * @param userId
     * @return
     */
    public List<String> selectRoleNameByUserId(Integer userId) {
        return sysRoleMapper.selectRoleNameByUserId(userId);
    }

    /**
     * 获得指定组织Id下所创建的所有角色的信息，包括其权限
     *
     * @param organizationId
     * @return
     */
    public List<AuthorityVO> selectAuthorityCreatedByOrganization(Integer organizationId) {
        //获得角色列表
        List<SysRole> roleList = sysRoleMapper.selectRoleByOrganizationId(organizationId);
        return selectAuthorityByRoleList(roleList);
    }


    /**
     * 根据角色列表查找权限信息,
     *
     * @param roleList
     * @return
     */
    public List<AuthorityVO> selectAuthorityByRoleList(List<SysRole> roleList) {
        List<AuthorityVO> authorityVOList = new LinkedList<>();
        for (SysRole sysRole : roleList) {
            List<PermissionVO> permissionVOList = sysPermissionMapper.selectPermissionByRoleId(sysRole.getId());
            authorityVOList.add(new AuthorityVO(sysRole.getId(), sysRole.getRoleName(), permissionVOList));
        }
        return authorityVOList;
    }

    /**
     * 获得该用户下所有的权限名称，用于shiro添加权限
     *
     * @param userId
     * @return
     */
    public List<String> selectPermissionNameByUserId(Integer userId) {
        //获得用户角色对应的权限
        List<PermissionVO> permissionVOList = selectPermissionVOByUseId(userId);
        List<String> permissionNameList = new LinkedList<>();
        while (null != permissionVOList && permissionVOList.size() != 0) {
            List<Integer> ids = new LinkedList<>();
            permissionVOList.forEach(permissionVO -> {
                ids.add(permissionVO.getPermissionId());
                permissionNameList.add(permissionVO.getPermissionName());
            });
            permissionVOList = selectPermissionByParent(ids);
        }
        return permissionNameList;
    }

    /**
     * 获得该用户角色对应的权限
     *
     * @param userId
     * @return
     */
    public List<PermissionVO> selectPermissionVOByUseId(Integer userId) {
        return sysPermissionMapper.selectPermissionVOByUserId(userId);
    }

    /**
     * 根据组织id查询用户信息
     *
     * @param adminOrganizationId
     * @param organizationId
     * @return
     */
    public List<SysUserVO> selectUserVOByOrganizationId(Integer adminOrganizationId, Integer organizationId) throws Exception {
        if (adminOrganizationId != 0 && !adminOrganizationId.equals(organizationId)) {
            throw new RequestParamErrorException("不能查看其它组织的管理员信息");
        }
        return sysUserMapper.selectUserVOByOrganizationId(organizationId);
    }


    /**
     * 根据权限id获取下一级子权限
     *
     * @param list
     * @return
     */
    public List<PermissionVO> selectPermissionByParent(List<Integer> list) {
        return sysPermissionMapper.selectPermissionVOByParentIds(list);
    }

    /**
     * 用于修改已经存在的后台用户的角色及userName,admin最高管理员的userId==1,角色不能修改
     *
     * @param organizationId
     * @param adminId
     * @param roleId
     * @param userId
     * @return
     * @throws Exception
     */
    public Boolean setUserRoleAndUserName(Integer organizationId, Integer adminId, Integer roleId, Integer userId, String userName, String email) throws Exception {
        SysUser user = sysUserMapper.selectUserById(userId);
        if (null == user) {
            throw new RequestParamErrorException("将要授权的用户不存在");
        }
        Integer userOrganizationId = user.getOrganizationId();
        //如果不是admin管理员，则只能对同一组织下的用户进行权限设定
        if (organizationId != 0 && !organizationId.equals(userOrganizationId)) {
            throw new RequestParamErrorException("您没有对该用户授予角色的权利");
        }
        SysRole sysRole = sysRoleMapper.selectRoleById(roleId);
        if (null == sysRole) {
            throw new RequestParamErrorException("还没有建立该角色，请先建立该角色");
        }
        //获得用户原有角色
        SysRole sysRoleOld = sysRoleMapper.selectRoleByUserId(userId);
        //如果原角色存在，则进行判断，是否有权更改该用户
        if (null != sysRoleOld) {
            if (sysRoleOld.getOrganizationId() == 0 && !sysRole.getId().equals(sysRoleOld.getId())) {
                throw new RequestParamErrorException("不能修改初始管理员的角色");
            }
            if (organizationId == 0 && user.getId() == 1 && !sysRole.getId().equals(sysRoleOld.getId())) {
                throw new RequestParamErrorException("admin初始超级管理员的角色不能修改:" + user.getUserName());
            }
            if (user.getId().equals(adminId) && !sysRole.getId().equals(sysRoleOld.getId())) {
                throw new RequestParamErrorException("不能设定自己的权限");
            }
        } else {
            if (sysRole.getUserId() == 0) {
                throw new RequestParamErrorException("不能将系统角色赋给他人");
            }
        }
        //获得该用户权限
        List<Integer> permissionIdList = sysRolePermissionMapper.selectPermissionIdsByUserId(adminId);
        //获得原角色的权限id
        List<Integer> rolePermissionIds = sysRolePermissionMapper.selectPermissionIdsByRoleId(roleId);
        if (!permissionIdList.containsAll(rolePermissionIds)) {
            throw new RequestParamErrorException("您的权限比原角色小，不能将该角色赋给他人");
        }
        List<Integer> userPermissionIds = sysRolePermissionMapper.selectPermissionIdsByUserId(userId);
        if (!permissionIdList.containsAll(userPermissionIds)) {
            throw new RequestParamErrorException("您的权限比原用户小，不能修改他的权限");
        }
        user.setUserName(userName);
        user.setEmail(email);
        sysUserMapper.updateByPrimaryKey(user);
        //如果之前该用户有角色对应关系，删除用户之前的角色对应关系
        Example exampleOne = new Example(SysUserRole.class);
        exampleOne.createCriteria().andEqualTo("userId", user.getId());
        sysUserRoleMapper.deleteByExample(exampleOne);
        SysUserRole sysUserRole = new SysUserRole(user.getId(), sysRole.getId());
        sysUserRoleMapper.insert(sysUserRole);
        return true;
    }

    /**
     * 按用户id列表批量删除用户及对应的角色关联信息，用户id为1的为admin最顶级管理员拥有最高权限不能删除，admin管理员创建的组织默认管理员组织内部无法删除
     *
     * @param organizationId
     * @param adminId
     * @param userIds
     * @return
     * @throws Exception
     */
    public Boolean deleteUserByUserId(Integer organizationId, Integer adminId, List<Integer> userIds) throws Exception {
        if (null != userIds && userIds.size() != 0) {
            for (Integer userId : userIds) {
                SysUser user = sysUserMapper.selectUserById(userId);
                //如果该用户不存在则跳过
                if (null == user) {
                    continue;
                }
                Integer userOrganizationId = user.getOrganizationId();
                //如果不是admin管理员，则只能对同一组织下的用户进行删除
                if (organizationId != 0 && !organizationId.equals(userOrganizationId)) {
                    throw new RequestParamErrorException("您没有对该用户删除的权利：" + user.getUserName());
                }
                if (organizationId == 0 && userId == 1) {
                    throw new RequestParamErrorException("admin初始超级管理员不能删除:" + user.getUserName());
                }
                if (userId.equals(adminId)) {
                    throw new RequestParamErrorException("不能删除自己");
                }
                //获得用户的角色
                SysRole sysRole = sysRoleMapper.selectRoleByUserId(userId);
                if (null != sysRole) {
                    if (sysRole.getOrganizationId() == 0 && organizationId != 0) {
                        throw new RequestParamErrorException("初始管理员不能被删除哦");
                    }
                }
                //获得该用户权限
                List<Integer> permissionIdList = sysRolePermissionMapper.selectPermissionIdsByUserId(adminId);
                //获得原角色的权限id
                List<Integer> userPermissionIds = sysRolePermissionMapper.selectPermissionIdsByUserId(user.getId());
                if (!permissionIdList.containsAll(userPermissionIds)) {
                    throw new RequestParamErrorException("您的权限比该用户小不能删除该用户：" + user.getUserName());
                }
                //删除该用户对应的角色对应关系
                Example exampleOne = new Example(SysUserRole.class);
                exampleOne.createCriteria().andEqualTo("userId", userId);
                sysUserRoleMapper.deleteByExample(exampleOne);
                //删除该用户
                sysUserMapper.delete(user);
            }
        }
        return true;
    }

    /**
     * 删除指定组织id的所有管理员
     *
     * @param adminOrganizationId
     * @param organizationId
     * @return
     * @throws Exception
     */
    public Integer deleteUserByOrganizationId(Integer adminOrganizationId, Integer organizationId) throws Exception {
        if (adminOrganizationId != 0) {
            throw new RequestParamErrorException("您没有删除指定组织所有管理员的权限");
        }
        if (organizationId == 0) {
            throw new RequestParamErrorException("admin管理员不能批量删除");
        }
        //删除用户角色对应关系
        sysUserRoleMapper.deleteUserRoleByOrganizationId(organizationId);
        //删除角色
        Example exampleTwo = new Example(SysRole.class);
        exampleTwo.createCriteria().andEqualTo("organizationId", organizationId);
        sysRoleMapper.deleteByExample(exampleTwo);
        //删除指定组织下所有用户
        Example example = new Example(SysUser.class);
        example.createCriteria().andEqualTo("organizationId", organizationId);
        return sysUserMapper.deleteByExample(example);
    }

    /**
     * 创建指定id的组织初始管理员，默认密码是123
     *
     * @param adminOrganizationId
     * @param modifyString
     * @param userName
     * @param loginName
     * @param email
     * @param enabled
     * @param organizationId
     * @return
     * @throws Exception
     */
    public Boolean createOrganizationAdmin(Integer adminOrganizationId, String modifyString, String userName, String loginName, String email, Integer enabled, Integer organizationId) throws Exception {
        Date modify = TimeUtil.parseStringToDateTime(modifyString);
        Date date = new Date();
        if (date.after(modify)) {
            throw new RequestParamErrorException("组织管理员的有效期不能在现在时间之前");
        }
        if (null == adminOrganizationId || adminOrganizationId != 0) {
            throw new RequestParamErrorException("你没有添加组织默认管理员的权限");
        }
        if (null == loginName || loginName.isEmpty()) {
            throw new RequestParamErrorException("登录名不能为空");
        }
        if (isExistedLoginName(loginName)) {
            throw new RequestParamErrorException("登陆名已经存在，请换个名字");
        }
        Organization organization = organizationMapper.selectOrganizationById(organizationId);
        if (null == organization) {
            throw new RequestParamErrorException("该组织不存在，无法创建初始管理员");
        }
        //处理组织初始管理员忘记密码，admin重新建立初始管理员的情况，会删除之前所有的管理员,防止之前的那个管理员占用管理员数量
        Example example = new Example(SysUser.class);
        example.createCriteria().andEqualTo("organizationId", organizationId);
        List<SysUser> sysUserList = sysUserMapper.selectByExample(example);
        if (null != sysUserList && sysUserList.size() != 0) {
            //删除用户角色对应关系
            sysUserRoleMapper.deleteUserRoleByOrganizationId(organizationId);
            for (SysUser sysUser : sysUserList) {
                sysUserMapper.deleteByExample(sysUser);
            }
        }
        //创建组织初始管理员
        String secret = myPasswordService.encryptPassword(loginName + defaultPassword);
        SysUser sysUser = new SysUser(date, modify, userName, loginName, secret, email, enabled, organizationId,0);
        sysUserMapper.insert(sysUser);
        Integer userId = sysUserMapper.selectByPrimaryKey(sysUser).getId();
        //赋予组织管理员角色
        SysUserRole sysUserRole = new SysUserRole(userId, 2);
        sysUserRoleMapper.insert(sysUserRole);
        return true;
    }

    /**
     * 设定组织的有效期
     *
     * @param adminOrganizationId
     * @param modifyString
     * @param organizationId
     * @return
     * @throws Exception
     */
    public Integer setModifyTimeByOrganizationId(Integer adminOrganizationId, String modifyString, Integer organizationId) throws Exception {
        Date modify = TimeUtil.parseStringToDateTime(modifyString);
        Date date = new Date();
        if (date.after(modify)) {
            throw new RequestParamErrorException("组织管理员的有效期不能在当前时间之前");
        }
        if (null == adminOrganizationId || adminOrganizationId != 0) {
            throw new RequestParamErrorException("你没有设定组织有效期的权限");
        }
        if (organizationId == 0) {
            throw new RequestParamErrorException("admin管理员的有效期不能设定");
        }
        return sysUserMapper.updateUserModifyTimeByOrganizationId(modify, organizationId);
    }

    /**
     * 设定指定组织所能创建的管理员个数
     *
     * @param adminOrganizationId
     * @param maxNumber
     * @param organizationId
     * @return
     * @throws Exception
     */
    public Integer setOrganizationMaxUserNumberByOrganizationId(Integer adminOrganizationId, Integer maxNumber, Integer organizationId) throws Exception {
        if (adminOrganizationId != 0) {
            throw new RequestParamErrorException("非admin管理员不能设定组织管理员的最大数量");
        }
        if (organizationId == 0) {
            throw new RequestParamErrorException("admin管理员的数量无限制，无需设定");
        }
        if (null == maxNumber) {
            throw new RequestParamErrorException("设定的管理员数量不能为空");
        }
        return organizationMapper.setOrganizationMaxUserNumberByOrganizationId(maxNumber, organizationId);
    }

    /**
     * 创建后台用户，初始密码123
     *
     * @param
     * @param userName
     * @param loginName
     * @param roleId
     * @param email
     * @return
     * @throws Exception
     */
    public Boolean createBackstageUserByRoleId(Integer adminUserId, String userName, String loginName, Integer roleId, String email) throws Exception {
        SysUser adminUser = sysUserMapper.selectUserById(adminUserId);
        if (null == loginName || loginName.isEmpty()) {
            throw new RequestParamErrorException("登录名不能为空");
        }
        if (isExistedLoginName(loginName)) {
            throw new RequestParamErrorException("登陆名已经存在，请换个名字");
        }
        SysRole sysRole = sysRoleMapper.selectRoleById(roleId);
        if (null == sysRole) {
            throw new RequestParamErrorException("该角色尚未创建，请先创建");
        }
        if (sysRole.getUserId() == 0 && adminUser.getId() != 1) {
            throw new RequestParamErrorException("系统管理员角色不能授权给其它后台用户");
        }
        //非admin管理员创建新用户需要对创建个数进行判断
        if (adminUser.getOrganizationId() != 0) {
            Integer number = sysUserMapper.countUserByOrganizationId(adminUser.getOrganizationId());
            Organization organization = organizationMapper.selectOrganizationById(adminUser.getOrganizationId());
            if (number >= organization.getMaxNumber()) {
                throw new RequestParamErrorException("您所在公司创建的管理员/后台用户个数已达上限：" + organization.getMaxNumber());
            }
        }
        //获得该用户权限
        List<Integer> permissionIdList = sysRolePermissionMapper.selectPermissionIdsByUserId(adminUserId);
        //获得原角色的权限id
        List<Integer> rolePermissionIds = sysRolePermissionMapper.selectPermissionIdsByRoleId(roleId);
        if (!permissionIdList.containsAll(rolePermissionIds)) {
            throw new RequestParamErrorException("您的权限比该角色拥有的权限小，不能将该角色赋给他人");
        }
        Date date = new Date();
        //新建后台用户
        String secret = myPasswordService.encryptPassword(loginName + defaultPassword);
        SysUser sysUser = new SysUser(date, adminUser.getModifyTime(), userName, loginName, secret, email, adminUser.getEnabled(), adminUser.getOrganizationId(),0);
        sysUserMapper.insert(sysUser);
        //添加用户角色关系
        Integer userId = sysUserMapper.selectByPrimaryKey(sysUser).getId();
        SysUserRole sysUserRole = new SysUserRole(userId, sysRole.getId());
        sysUserRoleMapper.insert(sysUserRole);
        return true;
    }

    /**
     * 批量解除或者禁用指定组织id的所有管理员
     *
     * @param adminOrganizationId
     * @param organizationId
     * @return
     * @throws Exception
     */
    public Integer setUserEnabledByOrganizationId(Integer adminOrganizationId, Integer organizationId, Integer state) throws Exception {
        if (adminOrganizationId!= 0) {
            throw new RequestParamErrorException("非admin管理员没有解除或禁用组织的权利");
        }
        if (null == state) {
            state = 0;
        }
        if (state > 1) {
            state = 1;
        }
        return sysUserMapper.setUserEnabledByOrganizationId(state, organizationId);
    }

    /**
     * 查询指定组织是否禁用
     *
     * @param adminOrganizationId
     * @param organizationId
     * @return
     * @throws Exception
     */
    public Integer getOrganizationUserEnabledStateByOrganizationId(Integer adminOrganizationId, Integer organizationId) throws Exception {
        if (adminOrganizationId != 0) {
            throw new RequestParamErrorException("非admin管理员没有解除或禁用组织的权利");
        }
        Example example = new Example(SysUser.class);
        example.createCriteria().andEqualTo("organizationId", organizationId);
        SysUser sysUser = sysUserMapper.selectOneByExample(example);
        if (null == sysUser) {
            throw new RequestParamErrorException("该组织不存在或者没有任何管理员");
        }
        return sysUser.getEnabled();
    }


    /**
     * 更改用户密码
     *
     * @param userId
     * @param oldSecret
     * @param newSecret
     * @return
     * @throws Exception
     */
    public Boolean resetPassword(Integer userId, String oldSecret, String newSecret) throws Exception {
        SysUser sysUser = sysUserMapper.selectUserById(userId);
        String string = sysUser.getLoginName() + oldSecret;
        if (!myPasswordService.passwordsMatch(string, sysUser.getUserPassword())) {
            throw new RequestParamErrorException("原密码错误");
        }
        if (null == newSecret || newSecret.isEmpty()) {
            throw new RequestParamErrorException("新密码不能为空");
        }
        String newString = sysUser.getLoginName() + newSecret;
        String secret = myPasswordService.encryptPassword(newString);
        sysUser.setUserPassword(secret);
        sysUserMapper.updateByPrimaryKey(sysUser);
        return true;
    }

    /**
     * 查询本组织下的管理员及权限信息
     *
     * @param organizationId
     * @return
     * @throws Exception
     */
    public List<UserAndAuthorityVO> selectUserAndAuthorityVO(Integer organizationId) throws Exception {
        List<UserAndAuthorityVO> list = new LinkedList<>();
        List<SysUserVO> sysUserVOList = sysUserMapper.selectUserVOByOrganizationId(organizationId);
        for (SysUserVO sysUserVO : sysUserVOList) {
            //获得用户所拥有的角色
            SysRole sysRole = sysRoleMapper.selectRoleByUserId(sysUserVO.getUserId());
            if (null != sysRole) {
                List<PermissionVO> mainPermissions = sysPermissionMapper.selectMainPermissionByRoleId(sysRole.getId());
                if (null != mainPermissions && mainPermissions.size() != 0) {
                    int size = mainPermissions.size();
                    StringBuilder mainPermissionNames = new StringBuilder();
                    mainPermissionNames.append(mainPermissions.get(0).getPermissionName());
                    for (int i = 1; i < size; i++) {
                        mainPermissionNames.append("|");
                        mainPermissionNames.append(mainPermissions.get(i).getPermissionName());
                    }
                    list.add(new UserAndAuthorityVO(sysUserVO.getUserId(), sysUserVO.getUserName(), sysUserVO.getLoginName(), sysRole.getId(), sysRole.getRoleName(), sysUserVO.getEmail(), mainPermissionNames.toString()));
                } else {
                    list.add(new UserAndAuthorityVO(sysUserVO.getUserId(), sysUserVO.getUserName(), sysUserVO.getLoginName(), sysRole.getId(), sysRole.getRoleName(), sysUserVO.getEmail(), null));
                }
            } else {
                //如果出现没有用户角色的数据也会呈现，便于管理员删除
                list.add(new UserAndAuthorityVO(sysUserVO.getUserId(), sysUserVO.getUserName(), sysUserVO.getLoginName(), null, null, sysUserVO.getEmail(), null));
            }
        }
        return list;
    }

    /**
     * 添加组织
     * @param organizationId
     * @param organizationName
     * @param maxNumber
     * @return
     * @throws Exception
     */
    public Boolean createOrganization(Integer organizationId,String organizationName,Integer maxNumber)throws Exception{
        if (null==organizationId||!organizationId.equals(0)){
            throw new RequestParamErrorException("您没有添加组织的权利");
        }
        Organization organization = new Organization(organizationName, maxNumber);
        organizationMapper.insert(organization);
        return true;
    }

    public Boolean isExistedLoginName(String loginName) {
        SysUser sysUser = getUser(loginName);
        if (null != sysUser) {
            return true;
        }
        return false;
    }

}
