package com.wangyb.learningdemo.authentication.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * @author wangyb
 * @Date 2019/5/6 14:41
 * Modified By:
 * Description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SysLogInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String operationType;
    private String operationName;
    private String createBy;
    private Date createDate;
    private String method;
    private Boolean result;

}
