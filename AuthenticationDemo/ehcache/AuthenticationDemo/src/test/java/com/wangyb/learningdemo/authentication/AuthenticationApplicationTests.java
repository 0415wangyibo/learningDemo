package com.wangyb.learningdemo.authentication;

import com.wangyb.learningdemo.authentication.entity.SysRole;
import com.wangyb.learningdemo.authentication.entity.SysRolePermission;
import com.wangyb.learningdemo.authentication.entity.SysUser;
import com.wangyb.learningdemo.authentication.entity.SysUserRole;
import com.wangyb.learningdemo.authentication.mapper.*;
import com.wangyb.learningdemo.authentication.service.MyPasswordService;
import com.wangyb.learningdemo.authentication.service.SysUserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import tk.mybatis.mapper.entity.Example;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AuthenticationApplicationTests {


	@Autowired
	private MyPasswordService myPasswordService;

	@Autowired
	private SysUserMapper sysUserMapper;

	@Autowired
	private SysUserRoleMapper sysUserRoleMapper;
	@Autowired
	private SysRoleMapper sysRoleMapper;
	@Autowired
	private SysUserService sysUserService;
	@Autowired
	private SysRolePermissionMapper sysRolePermissionMapper;

	@Test
	public void contextLoads() {
		String secret = myPasswordService.encryptPassword("wangyb" + "123");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String timeString = 2018 + "-" + 1 + "-" + 1 + " 00:00:00";
		Date time = sdf.parse(timeString, new ParsePosition(0));
		String dateString=  2200 + "-" + 1 + "-" + 1 + " 00:00:00";
		Date date = sdf.parse(dateString, new ParsePosition(0));
		SysUser sysUser = new SysUser(time, date, "wangyb", "wangyb", secret, "wangyb.com", 1, 0,0);
		sysUserMapper.insert(sysUser);
	}

}
