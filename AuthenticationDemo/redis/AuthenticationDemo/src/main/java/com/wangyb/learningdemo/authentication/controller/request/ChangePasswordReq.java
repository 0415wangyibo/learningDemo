package com.wangyb.learningdemo.authentication.controller.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created with Intellij IDEA.
 *
 * @author wangyb
 * @Date 2018/9/25 13:44
 * Modified By:
 * Description:用于接收修改密码的参数
 */
@Data
public class ChangePasswordReq {
    @ApiModelProperty(value = "原密码",required = true)
    private String oldSecret;
    @ApiModelProperty(value = "新密码",required = true)
    private String newSecret;

    public ChangePasswordReq(String oldSecret, String newSecret) {
        this.oldSecret = oldSecret;
        this.newSecret = newSecret;
    }
}
