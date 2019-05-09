package com.wangyb.learningdemo.authentication.controller.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created with Intellij IDEA.
 *
 * @author wangyb
 * @Date 2018/9/25 14:16
 * Modified By:
 * Description:用于设定组织管理员的有效期
 */
@Data
public class ModifySetReq {
    @ApiModelProperty(value = "yyyy-MM-dd HH:mm:ss 格式的时间",required = true)
    private String modifyString;
    @ApiModelProperty(value = "组织Id",required = true)
    private Integer organizationId;

    public ModifySetReq(String modifyString, Integer organizationId) {
        this.modifyString = modifyString;
        this.organizationId = organizationId;
    }
}
