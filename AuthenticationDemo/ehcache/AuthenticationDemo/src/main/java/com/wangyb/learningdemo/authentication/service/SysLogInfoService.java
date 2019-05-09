package com.wangyb.learningdemo.authentication.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.wangyb.learningdemo.authentication.entity.SysLogInfo;
import com.wangyb.learningdemo.authentication.mapper.SysLogInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author wangyb
 * @Date 2019/5/6 15:11
 * Modified By:
 * Description:
 */
@Service
@Transactional
public class SysLogInfoService {

    private SysLogInfoMapper sysLogInfoMapper;

    @Autowired
    public SysLogInfoService(SysLogInfoMapper sysLogInfoMapper) {
        this.sysLogInfoMapper = sysLogInfoMapper;
    }

    public Integer insertSysLog(SysLogInfo sysLogInfo) {
        return sysLogInfoMapper.insert(sysLogInfo);
    }

    @Transactional(readOnly = true)
    public PageInfo<SysLogInfo> listLog(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        PageHelper.orderBy("create_date desc");
        List<SysLogInfo> list = sysLogInfoMapper.selectAll();
        PageInfo<SysLogInfo> info = new PageInfo<>(list);
        return info;
    }
}
