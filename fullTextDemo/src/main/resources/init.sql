DROP TABLE IF EXISTS contact;

CREATE TABLE contact
(
  id               INT AUTO_INCREMENT
  COMMENT '家庭通讯录联系人id'
    PRIMARY KEY,
  create_time      TIMESTAMP DEFAULT CURRENT_TIMESTAMP NULL,
  update_time      TIMESTAMP                           NULL,
  name             VARCHAR(50)                         NULL
  COMMENT '名字',
  sex              VARCHAR(10)                         NULL
  COMMENT '性别',
  contact          VARCHAR(100)                        NULL
  COMMENT '联系方式',
  ch_position VARCHAR(100) DEFAULT '' NOT NULL
  COMMENT '职业中文名',
  position VARCHAR(100) DEFAULT '' NOT NULL
  COMMENT '职业或者职位，有无音调的拼音',
  position_abbr VARCHAR(100) DEFAULT ''NOT NULL
  COMMENT '职位拼音缩写，双倍',
  last_name VARCHAR(50) DEFAULT '' NOT NULL
  COMMENT '姓，有无音调的拼音',
  first_name VARCHAR(50) DEFAULT '' NOT NULL
  COMMENT '名，有无音调的拼音',
  abbr_name VARCHAR(50) DEFAULT '' NOT NULL
  COMMENT '名字，拼音缩写，双倍',
  FULLTEXT (position),
  FULLTEXT (position_abbr),
  FULLTEXT (last_name),
  FULLTEXT (first_name),
  FULLTEXT (abbr_name),
  FULLTEXT (last_name, first_name),
  FULLTEXT (last_name, position)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;


INSERT INTO full_text.contact (id, create_time, update_time, name, sex, contact, ch_position, position, position_abbr, last_name, first_name, abbr_name) VALUES (1, '2019-06-12 12:14:31', '2019-06-12 12:14:30', '张三', '男', '123456', '程序员', 'cheng xu yuan cheng2 xu4 yuan2', 'cxycxy', 'zhang zhang1', 'san san1', 'zszs');
INSERT INTO full_text.contact (id, create_time, update_time, name, sex, contact, ch_position, position, position_abbr, last_name, first_name, abbr_name) VALUES (2, '2019-06-12 13:48:38', '2019-06-12 13:48:38', '李四', '男', '7891011', '教师', 'jiao shi jiao4 shi1', 'jsjs', 'li li3', 'si si4', 'lsls');
INSERT INTO full_text.contact (id, create_time, update_time, name, sex, contact, ch_position, position, position_abbr, last_name, first_name, abbr_name) VALUES (3, '2019-06-12 13:49:12', '2019-06-12 13:49:12', '王五', '男', '12131415', '医生', 'yi sheng yi1 sheng1', 'ysys', 'wang wang2', 'wu wu3', 'wwww');
INSERT INTO full_text.contact (id, create_time, update_time, name, sex, contact, ch_position, position, position_abbr, last_name, first_name, abbr_name) VALUES (4, '2019-06-12 13:49:44', '2019-06-12 13:49:44', '钱六', '女', '16171819', '公务员', 'gong wu yuan gong1 wu4 yuan2', 'gwygwy', 'qian qian2', 'liu liu4', 'qlql');
INSERT INTO full_text.contact (id, create_time, update_time, name, sex, contact, ch_position, position, position_abbr, last_name, first_name, abbr_name) VALUES (5, '2019-06-12 13:50:08', '2019-06-12 13:50:08', '周星驰', '男', '20212223', '演员', 'yan yuan yan3 yuan2', 'yyyy', 'zhou zhou1', 'xing chi xing1 chi2', 'zxczxc');