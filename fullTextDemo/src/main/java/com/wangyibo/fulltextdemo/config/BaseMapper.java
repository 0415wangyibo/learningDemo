package com.wangyibo.fulltextdemo.config;

import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

/**
 * @author wangyb
 * @Date 2019/6/11 16:10
 * Modified By:
 * Description:
 */
public interface BaseMapper<T> extends Mapper<T>, MySqlMapper<T> {

}
