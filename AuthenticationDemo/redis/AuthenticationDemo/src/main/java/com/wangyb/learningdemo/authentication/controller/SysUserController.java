package com.wangyb.learningdemo.authentication.controller;

import com.wangyb.learningdemo.authentication.annotation.SysLog;
import com.wangyb.learningdemo.authentication.config.Constant;
import com.wangyb.learningdemo.authentication.controller.request.*;
import com.wangyb.learningdemo.authentication.controller.response.*;
import com.wangyb.learningdemo.authentication.pojo.RestResult;
import com.wangyb.learningdemo.authentication.service.SysRoleService;
import com.wangyb.learningdemo.authentication.service.SysUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Created with Intellij IDEA.
 *
 * @author wangyb
 * @Date 2018/9/3 14:06
 * Modified By:
 * Description:用户、权限接口
 */
@Api(tags = "权限管理demo，测试时请先登录获得token，访问时带上token")
@RestController
@CrossOrigin
@RequestMapping(value = "user")
public class SysUserController {

    private SysUserService sysUserService;
    private SysRoleService sysRoleService;

    @Autowired
    public SysUserController(SysUserService sysUserService, SysRoleService sysRoleService) {
        this.sysUserService = sysUserService;
        this.sysRoleService = sysRoleService;
    }

    @GetMapping("401")
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public RestResult<String> unauthorized() {
        String string = "没有通过认证";
        return new RestResult<>(string);
    }


    @ApiOperation(value = "组织：用户登录，如果登录成功则返回token及相关权限信息")
    @PostMapping("organization/login")
    public RestResult<LoginVO> organizationLogin(@RequestBody LoginReq loginReq) throws Exception {
        return new RestResult<>(sysUserService.organizationLogin(loginReq.getLoginName(), loginReq.getPassword()));
    }

    @SysLog(operationType = "查看", operationName = "管理员登录")
    @ApiOperation(value = "admin：用户登录，如果登录成功则返回token及相关权限信息")
    @PostMapping("admin/login")
    public RestResult<LoginVO> adminLogin(@RequestBody LoginReq loginReq) throws Exception {
        return new RestResult<>(sysUserService.adminLogin(loginReq.getLoginName(), loginReq.getPassword()));
    }

    @ApiOperation(value = "admin与组织：获得自己对应的权限")
    @GetMapping("permissions/user")
    public RestResult<List<PermissionVO>> selectPermissionVOByUseId(HttpServletRequest request) throws Exception {
        return new RestResult<>(sysUserService.selectPermissionVOByUseId((Integer) request.getAttribute(Constant.USER_ID)));
    }

    @RequiresPermissions("角色的查看")
    @ApiOperation(value = "admin与组织：获得该组织或admin所创建的管理员角色及权限")
    @GetMapping("role/permissions/organization")
    public RestResult<List<AuthorityVO>> selectAuthorityCreatedByOrganization(HttpServletRequest request) throws Exception {
        return new RestResult<>(sysUserService.selectAuthorityCreatedByOrganization((Integer) request.getAttribute(Constant.ORGANIZATION_ID)));
    }

    @RequiresPermissions("后台用户的添加")
    @ApiOperation(value = "admin与组织：获得该组织或admin所创建的管理员角色及Id，用于添加后台用户时选择角色使用")
    @GetMapping("role/organization")
    public RestResult<List<NameAndIdVO>> selectRoleNameAndIdByOrganizationId(HttpServletRequest request) throws Exception {
        return new RestResult<>(sysRoleService.selectRoleNameAndIdByOrganizationId((Integer) request.getAttribute(Constant.ORGANIZATION_ID)));
    }

    @RequiresPermissions("查看组织")
    @ApiOperation(value = "admin:查询指定组织下的用户信息")
    @ApiImplicitParam(name = "organizationId", value = "组织id", required = true, dataType = "int", paramType = "query")
    @GetMapping("user/info/admin")
    public RestResult<List<SysUserVO>> selectUserVOByOrganizationId(@RequestParam("organizationId") Integer organizationId, HttpServletRequest request) throws Exception {
        return new RestResult<>(sysUserService.selectUserVOByOrganizationId((Integer) request.getAttribute(Constant.ORGANIZATION_ID), organizationId));
    }


    @RequiresPermissions("后台用户的编辑")
    @ApiOperation(value = "admin与组织：根据roleId，userId修改已经存在的后台用户")
    @PostMapping("backstage/user/edit")
    public RestResult<Boolean> setUserRoleAndUserName(@RequestBody UserEditReq userEditReq, HttpServletRequest request) throws Exception {
        return new RestResult<>(sysUserService.setUserRoleAndUserName((Integer) request.getAttribute(Constant.ORGANIZATION_ID), (Integer) request.getAttribute(Constant.USER_ID), userEditReq.getRoleId(), userEditReq.getUserId(), userEditReq.getUserName(), userEditReq.getEmail()));
    }

    @RequiresPermissions("后台用户的删除")
    @ApiOperation(value = "admin与组织:按用户id列表批量删除用户及对应的角色关联信息")
    @DeleteMapping("users/ids")
    public RestResult<Boolean> deleteUserByUserId(@RequestBody IdListReq idListReq, HttpServletRequest request) throws Exception {
        System.out.println("用户Id列表" + idListReq.getList());
        return new RestResult<>(sysUserService.deleteUserByUserId((Integer) request.getAttribute(Constant.ORGANIZATION_ID), (Integer) request.getAttribute(Constant.USER_ID), idListReq.getList()));
    }

    @RequiresPermissions("创建组织初始管理员")
    @ApiOperation(value = "admin:创建组织的初始管理员")
    @PostMapping("organization/admin/create")
    public RestResult<Boolean> createOrganizationAdmin(@RequestBody AdminCreateReq adminCreateReq, HttpServletRequest request) throws Exception {
        return new RestResult<>(sysUserService.createOrganizationAdmin((Integer) request.getAttribute(Constant.ORGANIZATION_ID), adminCreateReq.getModifyString(), adminCreateReq.getUserName(), adminCreateReq.getLoginName(), adminCreateReq.getEmail(), adminCreateReq.getEnabled(), adminCreateReq.getOrganizationId()));
    }

    @RequiresPermissions("设定组织管理员有效期")
    @ApiOperation(value = "admin:设定组织管理员的有效期，返回更新有效期的管理员个数")
    @PostMapping("organization/modify")
    public RestResult<Integer> setModifyTimeByOrganizationId(@RequestBody ModifySetReq modifySetReq, HttpServletRequest request) throws Exception {
        return new RestResult<>(sysUserService.setModifyTimeByOrganizationId((Integer) request.getAttribute(Constant.ORGANIZATION_ID), modifySetReq.getModifyString(), modifySetReq.getOrganizationId()));
    }

    @RequiresPermissions("后台用户的添加")
    @ApiOperation(value = "admin与组织:添加后台用户，初始密码123")
    @PostMapping("backstage/user/add")
    public RestResult<Boolean> createBackstageUserByRoleId(@RequestBody UserAddReq userAddReq, HttpServletRequest request) throws Exception {
        return new RestResult<>(sysUserService.createBackstageUserByRoleId((Integer) request.getAttribute(Constant.USER_ID), userAddReq.getUserName(), userAddReq.getLoginName(), userAddReq.getRoleId(), userAddReq.getEmail()));
    }

    @RequiresPermissions("编辑组织管理员状态")
    @ApiOperation(value = "admin:解除或者禁用指定组织id的所有管理员")
    @PostMapping("organization/enabled")
    public RestResult<Integer> setUserEnabledTrueByOrganizationId(@RequestBody OrganizationEnableReq organizationEnableReq, HttpServletRequest request) throws Exception {
        return new RestResult<>(sysUserService.setUserEnabledByOrganizationId((Integer) request.getAttribute(Constant.ORGANIZATION_ID), organizationEnableReq.getOrganizationId(), organizationEnableReq.getState()));
    }

    @RequiresPermissions("查看组织管理员状态")
    @ApiOperation(value = "admin:查询指定组织id的管理员是否被禁用")
    @ApiImplicitParam(name = "organizationId", value = "组织id", required = true, dataType = "int", paramType = "query")
    @GetMapping("organization/enabled")
    public RestResult<Integer> getOrganizationUserEnabledStateByOrganizationId(@RequestParam("organizationId") Integer organizationId, HttpServletRequest request) throws Exception {
        return new RestResult<>(sysUserService.getOrganizationUserEnabledStateByOrganizationId((Integer) request.getAttribute(Constant.ORGANIZATION_ID), organizationId));
    }

    @RequiresPermissions("设定组织管理员个数")
    @ApiOperation(value = "admin:设定指定组织所能创建的管理员数量")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "maxNumber", value = "管理员最大数量", required = true, dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "organizationId", value = "组织id", required = true, dataType = "int", paramType = "query")
    })
    @PostMapping("organization/number")
    public RestResult<Integer> setOrganizationMaxUserNumberByOrganizationId(@RequestParam("maxNumber") Integer maxNumber, @RequestParam("organizationId") Integer organizationId,
                                                                    HttpServletRequest request) throws Exception {
        return new RestResult<>(sysUserService.setOrganizationMaxUserNumberByOrganizationId((Integer) request.getAttribute(Constant.ORGANIZATION_ID), maxNumber, organizationId));
    }

    @ApiOperation(value = "admin与组织:更改用户密码(更改密码后需要重新登录)")
    @PostMapping("user/password")
    public RestResult<Boolean> resetPassword(@RequestBody ChangePasswordReq changePasswordReq, HttpServletRequest request) throws Exception {
        return new RestResult<>(sysUserService.resetPassword((Integer) request.getAttribute(Constant.USER_ID), changePasswordReq.getOldSecret(), changePasswordReq.getNewSecret()));
    }

    @RequiresPermissions("后台用户的查看")
    @ApiOperation(value = "admin与组织:查看后台用户")
    @GetMapping("user/authority/organization")
    public RestResult<List<UserAndAuthorityVO>> selectUserAndAuthorityVOBySelf(HttpServletRequest request) throws Exception {
        return new RestResult<>(sysUserService.selectUserAndAuthorityVO((Integer) request.getAttribute(Constant.ORGANIZATION_ID)));
    }

    @RequiresPermissions("删除组织所有管理员")
    @ApiOperation(value = "admin:删除指定组织的所有管理员信息")
    @ApiImplicitParam(name = "organizationId", value = "组织id", required = true, dataType = "int", paramType = "query")
    @DeleteMapping("users/organization")
    public RestResult<Integer> deleteUserByOrganizationId(@RequestParam("organizationId") Integer organizationId, HttpServletRequest request) throws Exception {
        return new RestResult<>(sysUserService.deleteUserByOrganizationId((Integer) request.getAttribute(Constant.ORGANIZATION_ID), organizationId));
    }

    @RequiresPermissions("角色的添加")
    @ApiOperation(value = "admin与组织：通过权限id列表添加角色，如果自身没有该权限则不会添加该权限")
    @PostMapping(value = "/role/add")
    public RestResult<Boolean> addRole(@RequestBody RoleAddReq roleAddReq, HttpServletRequest request) throws Exception {
        return new RestResult<>(sysRoleService.addRole((Integer) request.getAttribute(Constant.ORGANIZATION_ID), (Integer) request.getAttribute(Constant.USER_ID), roleAddReq.getRoleName(),roleAddReq.getMemo(), roleAddReq.getPermissionIds()));
    }

    @RequiresPermissions("角色的编辑")
    @ApiOperation(value = "admin与组织：通过roleId及权限id列表更新角色")
    @PostMapping(value = "/role/edit")
    public RestResult<Boolean> changeRoleByRoleIdAndPermissionIds(@RequestBody RoleEditReq roleEditReq, HttpServletRequest request) throws Exception {
        return new RestResult<>(sysRoleService.changeRoleByRoleIdAndPermissionIds((Integer) request.getAttribute(Constant.ORGANIZATION_ID), (Integer) request.getAttribute(Constant.USER_ID), roleEditReq.getRoleId(), roleEditReq.getRoleName(),roleEditReq.getMemo(), roleEditReq.getPermissionIds()));
    }

    @RequiresPermissions("角色的删除")
    @ApiOperation(value = "admin与组织:删除角色，同时会将之前绑定该角色的用户的角色关系清空")
    @DeleteMapping(value = "role")
    public RestResult<Boolean> deleteRole(@RequestParam("id") Integer id, HttpServletRequest request) throws Exception {
        return new RestResult<>(sysRoleService.deleteRole((Integer) request.getAttribute(Constant.ORGANIZATION_ID), (Integer) request.getAttribute(Constant.USER_ID), id));
    }

    @RequiresPermissions("创建组织")
    @ApiOperation(value = "admin:创建新的组织")
    @DeleteMapping(value = "organization/create")
    public RestResult<Boolean> createOrganization(String organizationName,Integer maxNumber,HttpServletRequest request)throws Exception{
        return new RestResult<>(sysUserService.createOrganization((Integer) request.getAttribute(Constant.ORGANIZATION_ID), organizationName, maxNumber));
    }
}
