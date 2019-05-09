package com.wangyb.learningdemo.authentication.controller.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created with Intellij IDEA.
 *
 * @author wangyb
 * @Date 2018/9/25 14:22
 * Modified By:
 * Description:用于接收设定组织是否禁用的参数
 */
@Data
public class OrganizationEnableReq {
    @ApiModelProperty(value = "组织Id",required = true)
    private Integer organizationId;
    @ApiModelProperty(value = "0：禁用；1：解除禁用",required = true)
    private Integer state;

    public OrganizationEnableReq(Integer organizationId, Integer state) {
        this.organizationId = organizationId;
        this.state = state;
    }
}
