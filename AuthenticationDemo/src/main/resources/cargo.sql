/**
 注意：下面的sql语句中含有变量，这里仅仅写sql语句，变量需要结合mybatis、hibernate等数据库操作框架进行传入
 表中的时间，主要用于限制查找范围，暂时未对查找范围进行限制；
 用户及权限等表的增删改差省略，都差不多，需要结合具体的权限设计要求设计；
 */
/**
 创建厂商表
 */
CREATE TABLE manufacture
(
  id              INT AUTO_INCREMENT
  PRIMARY KEY,
  create_time     DATETIME     NULL
  COMMENT '创建时间',
  modify_time     DATETIME     NULL
  COMMENT '修改时间',
  manufacture_name VARCHAR(50)  NULL
  COMMENT '厂商名',
  manufacture_code VARCHAR(100)  NULL
  COMMENT '厂商唯一编码',
  address  VARCHAR(200) NULL
  COMMENT '厂商地址'
)
  ENGINE = InnoDB;

create INDEX manufacture_name on manufacture(manufacture_name);
create INDEX address on manufacture(address);

/**
新建厂商
 */
insert into manufacture(create_time,modify_time,manufacture_name,manufacture_code,address)
values(#{createTime,jdbcType=TIMESTAMP},#{modifyTime,jdbcType=TIMESTAMP},#{manufactureName},#{manufactureCode},#{address});
/*
根据厂商名查找厂商
 */
select *
from manufacture
where manufacture_name = #{manufactureName};
/**
根据厂商地址查找厂商
 */
select *
from manufacture
where address = #{address};
/**
根据id删除厂商
 */
delete
from manufacture
where id = #{id};

/**
 创建货物
 */
CREATE TABLE cargo
(
  id              INT AUTO_INCREMENT
    PRIMARY KEY,
  create_time     DATETIME     NULL
  COMMENT '创建时间',
  modify_time     DATETIME     NULL
  COMMENT '修改时间',
  cargo_name VARCHAR(50)  NULL
  COMMENT '货物名',
  type  VARCHAR(50) NULL
  COMMENT '货物类别',
  manufacture_id INT NOT NULL
  COMMENT '货物所属厂商id',
  state   INT          NULL
  COMMENT '货物状态，0-进库，1出库'

)
  ENGINE = InnoDB;

create INDEX cargo_name on cargo(cargo_name);
create INDEX manufacture_id on cargo(manufacture_id);
create INDEX state on cargo(state);

/*
增加货物
 */
insert into cargo(create_time,modify_time,cargo_name,type,manufacture_id,state)
values(#{createTime,jdbcType=TIMESTAMP},#{modifyTime,jdbcType=TIMESTAMP},#{cargoName},#{type},#{manufactureId},#{state});
/**
根据厂商id查找货物
 */
select *
from cargo
where manufacture_id = #{manufactureId};
/**
根据货物名查找货物
 */
select *
from cargo
where cargo_name = #{cargoName};
/**
查找指定货物的厂商
 */
select m.manufacture_name
from manufacture m,cargo car
where car.id = #{id} and car.manufacture_id = m.id;
/**
修改指定货物的进库或者出库状态
 */
update cargo
set state = #{state}
where id = #{id};
/**
查找指定状态的货物
 */
select *
from cargo
where state = #{state};
/*
创建用户表
*/
CREATE TABLE sys_user
(
  id              INT AUTO_INCREMENT
    PRIMARY KEY,
  create_time     DATETIME     NULL
  COMMENT '创建时间',
  modify_time     DATETIME     NULL
  COMMENT '修改时间',
  user_name       VARCHAR(255) NULL
  COMMENT '用户名',
  login_name      VARCHAR(255) NULL
  COMMENT '登录名',
  user_password   CHAR(133)    NULL
  COMMENT '用户密码',
  email           VARCHAR(100) NULL
  COMMENT '邮箱',
  enabled         TINYINT      NULL
  COMMENT '是否禁用',
  token_version   INT          NULL
  COMMENT 'token版本号，进一步判断token是否过期'
)
  ENGINE = InnoDB;

create INDEX login_name on sys_user(login_name);

/*
 用户角色关联表
 */
CREATE TABLE sys_user_role
(
  user_id INT NULL,
  role_id INT NULL
)
  ENGINE = InnoDB;

/*
权限表
 */
CREATE TABLE sys_permission
(
  id              INT AUTO_INCREMENT
    PRIMARY KEY,
  permission_name VARCHAR(50)  NULL
  COMMENT '权限名，可以使用注解的方式，为每个controller标记权限',
  permission_url  VARCHAR(200) NULL
  COMMENT '权限对应的url',
  parent_id       INT          NULL
  COMMENT '父权限id'
)
  ENGINE = InnoDB;

create INDEX parent_id on sys_permission(parent_id);
create INDEX permission_name on sys_permission(permission_name);

/*
 角色表
 */
CREATE TABLE sys_role
(
  id              INT AUTO_INCREMENT
    PRIMARY KEY,
  role_name       VARCHAR(50) NULL
  COMMENT '管理员角色名称',
  memo            VARCHAR(50) NULL
  COMMENT '角色描述',
  user_id         INT         NULL
  COMMENT '用于标记该角色由哪个用户（管理员）创建，用户可以创建自己权限范围内的任意角色'
)
  ENGINE = InnoDB;

/**
角色权限关联表
 */
CREATE TABLE sys_role_permission
(
  role_id       INT NULL,
  permission_id INT NULL
)
  ENGINE = InnoDB;

/**
 查找某个用户所拥有的主权限
 */
select p.*
from sys_user u,sys_user_role ur,sys_role_permission rp,sys_permission p
where u.id = #{id} and u.id = ur.user_id and ur.role_id = rp.role_id and rp.permission_id = p.id