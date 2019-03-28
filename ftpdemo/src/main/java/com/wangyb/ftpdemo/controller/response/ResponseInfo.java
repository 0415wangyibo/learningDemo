package com.wangyb.ftpdemo.controller.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * Created with Intellij IDEA.
 *
 * @author wangyb
 * @Date 2019/2/16 15:07
 * Modified By:
 * Description: 用于返回数据结果使用
 */
@Data
public class ResponseInfo implements Serializable {

    private static final long serialVersionUID = 5230828756597912974L;

    @ApiModelProperty(value = "是否成功")
    private Boolean ifOk;
    @ApiModelProperty(value = "原因")
    private String reason;
}
