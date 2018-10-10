package com.wangyb.learningdemo.authentication.config;

import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

/**
 * Created with Intellij IDEA.
 *
 * @author wangyb
 * @Date 2018/10/8 16:04
 * Modified By:
 * Description:
 */

public interface BaseMapper<T> extends Mapper<T>, MySqlMapper<T> {

}
