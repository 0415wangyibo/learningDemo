package com.wangyb.learningdemo.authentication.mapper;

import com.wangyb.learningdemo.authentication.config.BaseMapper;
import com.wangyb.learningdemo.authentication.entity.SysLogInfo;
import org.apache.ibatis.annotations.CacheNamespaceRef;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author wangyb
 * @Date 2019/5/6 14:57
 * Modified By:
 * Description:
 */
@Mapper
@CacheNamespaceRef(SysLogInfoMapper.class)
public interface SysLogInfoMapper extends BaseMapper<SysLogInfo> {

}
