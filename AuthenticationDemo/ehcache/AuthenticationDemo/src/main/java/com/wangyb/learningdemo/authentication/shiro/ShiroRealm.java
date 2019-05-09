package com.wangyb.learningdemo.authentication.shiro;

import com.wangyb.learningdemo.authentication.entity.SysUser;
import com.wangyb.learningdemo.authentication.service.SysUserService;
import com.wangyb.learningdemo.authentication.utils.JwtUtil;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

/**
 * Created with Intellij IDEA.
 *
 * @author wangyb
 * @Date 2018/10/8 16:15
 * Modified By:
 * Description:
 */
public class ShiroRealm extends AuthorizingRealm{

    @Autowired
    private SysUserService sysUserService;

    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof JwtToken;
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        //得到的是登录名
        Integer userId = JwtUtil.getUserId(principals.toString());
        //获得角色列表
        Set<String> roleSet = new HashSet<>(sysUserService.selectRoleNameByUserId(userId));
        //获得权限列表
        Set<String> permissionSet = new HashSet<>(sysUserService.selectPermissionNameByUserId(userId));
        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
        simpleAuthorizationInfo.setRoles(roleSet);
        simpleAuthorizationInfo.setStringPermissions(permissionSet);
        return simpleAuthorizationInfo;

    }

    /**
     * 验证用户名正确与否
     *
     * @param auth
     * @return
     * @throws AuthenticationException
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken auth) throws AuthenticationException {
        String token = (String) auth.getCredentials();
        String loginName = JwtUtil.getLoginName(token);
        Integer tokenVersion = JwtUtil.getTokenVersion(token);
        if (null == loginName || loginName.isEmpty() || null == tokenVersion) {
            throw new AuthenticationException("token无效");
        }
        SysUser user = sysUserService.getUser(loginName);
        if (null == user) {
            throw new AuthenticationException("用户不存在");
        }
        if (user.getEnabled() == 0) {
            throw new LockedAccountException("账户锁定，请联系管理员");
        }
        Date date = new Date();
        if (user.getModifyTime().before(date)) {
            throw new LockedAccountException("账户已过有效期，请联系admin管理员");
        }
        if (!tokenVersion.equals(user.getTokenVersion())) {
            throw new AuthenticationException("您的账号在别处登录，token已经失效");
        }
        if (!JwtUtil.verify(token, loginName, user.getId(), user.getOrganizationId(), tokenVersion, user.getUserPassword())) {
            throw new AuthenticationException("用户名或密码错误");
        }
        return new SimpleAuthenticationInfo(token, token, "my_realm");
    }
}
