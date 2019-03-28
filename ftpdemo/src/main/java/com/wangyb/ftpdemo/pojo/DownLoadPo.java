package com.wangyb.ftpdemo.pojo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Created with Intellij IDEA.
 *
 * @author wangyb
 * @Date 2019/1/29 15:14
 * Modified By:
 * Description: 便于将数据存入配置文件
 */
@Data
public class DownLoadPo implements Serializable{

    private static final long serialVersionUID = 1L;

    private List<DayDownLoadInfo> dayDownLoadInfoList;
}
