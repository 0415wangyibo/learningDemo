CREATE TABLE organization
(
  id                INT AUTO_INCREMENT
    PRIMARY KEY,
  organization_name VARCHAR(255) NULL
  COMMENT '组织的名字',
  max_number        INT          NULL
  COMMENT '同一组织最多能创建的管理员个数'
)
  ENGINE = InnoDB;

CREATE TABLE sys_permission
(
  id              INT AUTO_INCREMENT
    PRIMARY KEY,
  permission_name VARCHAR(50)  NULL,
  permission_url  VARCHAR(200) NULL,
  parent_id       INT          NULL
)
  ENGINE = InnoDB;

CREATE TABLE sys_role
(
  id              INT AUTO_INCREMENT
    PRIMARY KEY,
  role_name       VARCHAR(50) NULL
  COMMENT '管理员角色名称',
  memo            VARCHAR(50) NULL
  COMMENT '角色描述',
  organization_id INT         NULL
  COMMENT '用于标记该角色由哪个组织创建',
  user_id         INT         NULL
  COMMENT '用于标记该角色由哪个用户（管理员）创建'
)
  ENGINE = InnoDB;

CREATE TABLE sys_role_permission
(
  role_id       INT NULL,
  permission_id INT NULL
)
  ENGINE = InnoDB;


CREATE TABLE sys_user
(
  id              INT AUTO_INCREMENT
    PRIMARY KEY,
  create_time     DATETIME     NULL,
  modify_time     DATETIME     NULL,
  user_name       VARCHAR(255) NULL
  COMMENT '用户名',
  login_name      VARCHAR(255) NULL
  COMMENT '登录名',
  user_password   CHAR(133)    NULL,
  email           VARCHAR(100) NULL,
  enabled         TINYINT      NULL,
  organization_id INT          NULL,
  token_version   INT          NULL
)
  ENGINE = InnoDB;

CREATE TABLE sys_user_role
(
  user_id INT NULL,
  role_id INT NULL
)
  ENGINE = InnoDB;

CREATE TABLE sys_log_info
(
  operation_type VARCHAR(255)                        NULL,
  operation_name VARCHAR(255)                        NULL,
  create_by      VARCHAR(255)                        NULL,
  create_date    TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP,
  method         VARCHAR(255)                        NULL,
  result         TINYINT(1)                          NULL
)
  COMMENT '系统日志'
  ENGINE = InnoDB;

INSERT organization (id, organization_name, max_number) VALUES (1, '腾讯', 8);
INSERT organization (id, organization_name, max_number) VALUES (2, '谷歌', 8);

INSERT sys_user (id, create_time, modify_time, user_name, login_name, user_password, email, enabled, organization_id, token_version)
VALUES (1, '2018-01-03 00:00:00', '2200-09-30 00:00:00', 'wangyb', 'wangyb',
        'rS9wVxfYrZPRaE7aFlLWTehZioqMn/kXYxg3J9a4MjZSNs+QAwQTk5daJQhfwd/1sTjf/oK78cqDdoDn6J5FBA==$hwiBTZwijQAe2EIMOtNjDQ==',
        'wangyb.com', 1, 0, 0);

INSERT sys_user_role (user_id, role_id) VALUES (1, 1);


INSERT sys_role (id, role_name, memo, organization_id, user_id) VALUES (1, 'admin超级管理员', 'admin初始管理员角色', 0, 0);
INSERT sys_role (id, role_name, memo, organization_id, user_id) VALUES (2, '组织超级管理员', '组织超级管理员角色', 0, 0);

# admin超级管理员权限绑定
INSERT sys_role_permission (role_id, permission_id) VALUES (1, 12);
INSERT sys_role_permission (role_id, permission_id) VALUES (1, 13);
INSERT sys_role_permission (role_id, permission_id) VALUES (1, 14);
INSERT sys_role_permission (role_id, permission_id) VALUES (1, 15);
INSERT sys_role_permission (role_id, permission_id) VALUES (1, 16);
INSERT sys_role_permission (role_id, permission_id) VALUES (1, 17);
INSERT sys_role_permission (role_id, permission_id) VALUES (1, 18);
INSERT sys_role_permission (role_id, permission_id) VALUES (1, 19);
INSERT sys_role_permission (role_id, permission_id) VALUES (1, 20);
INSERT sys_role_permission (role_id, permission_id) VALUES (1, 21);

# 组织超级管理员角色绑定
INSERT sys_role_permission (role_id, permission_id) VALUES (2, 6);
INSERT sys_role_permission (role_id, permission_id) VALUES (2, 7);
INSERT sys_role_permission (role_id, permission_id) VALUES (2, 8);
INSERT sys_role_permission (role_id, permission_id) VALUES (2, 9);
INSERT sys_role_permission (role_id, permission_id) VALUES (2, 10);
INSERT sys_role_permission (role_id, permission_id) VALUES (2, 11);

# 组织主权限
INSERT sys_permission (id, permission_name, permission_url, parent_id) VALUES (1, '角色管理', NULL, 0);
INSERT sys_permission (id, permission_name, permission_url, parent_id) VALUES (2, '用户管理', NULL, 0);

# admin主权限
INSERT sys_permission (id, permission_name, permission_url, parent_id) VALUES (3, '组织管理', NULL, 0);
INSERT sys_permission (id, permission_name, permission_url, parent_id) VALUES (4, '角色管理', NULL, 0);
INSERT sys_permission (id, permission_name, permission_url, parent_id) VALUES (5, '用户管理', NULL, 0);

# 组织一级子权限
INSERT sys_permission (id, permission_name, permission_url, parent_id) VALUES (6, '查看', NULL, 1);
INSERT sys_permission (id, permission_name, permission_url, parent_id) VALUES (7, '编辑', NULL, 1);
INSERT sys_permission (id, permission_name, permission_url, parent_id) VALUES (8, '删除', NULL, 1);
INSERT sys_permission (id, permission_name, permission_url, parent_id) VALUES (9, '查看', NULL, 2);
INSERT sys_permission (id, permission_name, permission_url, parent_id) VALUES (10, '编辑', NULL, 2);
INSERT sys_permission (id, permission_name, permission_url, parent_id) VALUES (11, '删除', NULL, 2);

# admin一级子权限
INSERT sys_permission (id, permission_name, permission_url, parent_id) VALUES (12, '查看', NULL, 3);
INSERT sys_permission (id, permission_name, permission_url, parent_id) VALUES (13, '添加', NULL, 3);
INSERT sys_permission (id, permission_name, permission_url, parent_id) VALUES (14, '编辑', NULL, 3);
INSERT sys_permission (id, permission_name, permission_url, parent_id) VALUES (15, '删除', NULL, 3);
INSERT sys_permission (id, permission_name, permission_url, parent_id) VALUES (16, '查看', NULL, 4);
INSERT sys_permission (id, permission_name, permission_url, parent_id) VALUES (17, '编辑', NULL, 4);
INSERT sys_permission (id, permission_name, permission_url, parent_id) VALUES (18, '删除', NULL, 4);
INSERT sys_permission (id, permission_name, permission_url, parent_id) VALUES (19, '查看', NULL, 5);
INSERT sys_permission (id, permission_name, permission_url, parent_id) VALUES (20, '编辑', NULL, 5);
INSERT sys_permission (id, permission_name, permission_url, parent_id) VALUES (21, '删除', NULL, 5);

# 组织具体权限
INSERT sys_permission (id, permission_name, permission_url, parent_id) VALUES (22, '角色的查看', NULL, 6);
INSERT sys_permission (id, permission_name, permission_url, parent_id) VALUES (23, '角色的添加', NULL, 7);
INSERT sys_permission (id, permission_name, permission_url, parent_id) VALUES (24, '角色的编辑', NULL, 7);
INSERT sys_permission (id, permission_name, permission_url, parent_id) VALUES (25, '角色的删除', NULL, 8);
INSERT sys_permission (id, permission_name, permission_url, parent_id) VALUES (26, '后台用户的查看', NULL, 9);
INSERT sys_permission (id, permission_name, permission_url, parent_id) VALUES (27, '后台用户的添加', NULL, 10);
INSERT sys_permission (id, permission_name, permission_url, parent_id) VALUES (28, '后台用户的编辑', NULL, 10);
INSERT sys_permission (id, permission_name, permission_url, parent_id) VALUES (29, '后台用户的删除', NULL, 11);

# admin具体权限
INSERT sys_permission (id, permission_name, permission_url, parent_id) VALUES (30, '查看组织', NULL, 12);
INSERT sys_permission (id, permission_name, permission_url, parent_id) VALUES (31, '查看组织管理员状态', NULL, 12);
INSERT sys_permission (id, permission_name, permission_url, parent_id) VALUES (32, '创建组织初始管理员', NULL, 13);
INSERT sys_permission (id, permission_name, permission_url, parent_id) VALUES (33, '设定组织管理员有效期', NULL, 14);
INSERT sys_permission (id, permission_name, permission_url, parent_id) VALUES (34, '编辑组织管理员状态', NULL, 14);
INSERT sys_permission (id, permission_name, permission_url, parent_id) VALUES (35, '设定组织管理员个数', NULL, 14);
INSERT sys_permission (id, permission_name, permission_url, parent_id) VALUES (36, '删除组织所有管理员', NULL, 15);
INSERT sys_permission (id, permission_name, permission_url, parent_id) VALUES (37, '角色的查看', NULL, 16);
INSERT sys_permission (id, permission_name, permission_url, parent_id) VALUES (38, '角色的添加', NULL, 17);
INSERT sys_permission (id, permission_name, permission_url, parent_id) VALUES (39, '角色的编辑', NULL, 17);
INSERT sys_permission (id, permission_name, permission_url, parent_id) VALUES (40, '角色的删除', NULL, 18);
INSERT sys_permission (id, permission_name, permission_url, parent_id) VALUES (41, '后台用户的查看', NULL, 19);
INSERT sys_permission (id, permission_name, permission_url, parent_id) VALUES (42, '后台用户的添加', NULL, 20);
INSERT sys_permission (id, permission_name, permission_url, parent_id) VALUES (43, '后台用户的编辑', NULL, 20);
INSERT sys_permission (id, permission_name, permission_url, parent_id) VALUES (44, '后台用户的删除', NULL, 21);
INSERT sys_permission (id, permission_name, permission_url, parent_id) VALUES (45, '创建组织', NULL, 13);
