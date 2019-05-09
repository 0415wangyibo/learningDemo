package com.wangyb.learningdemo.authentication.controller.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created with Intellij IDEA.
 *
 * @author wangyb
 * @Date 2018/9/15 17:52
 * Modified By:
 * Description:用户返回筛选条件及id
 */
@Data
public class NameAndIdVO {
    @ApiModelProperty(value = "name对应的id")
    private Integer nameId;
    @ApiModelProperty(value = "需要返回给前端在页面上呈现的String")
    private String name;

    public NameAndIdVO(Integer nameId, String name) {
        this.nameId = nameId;
        this.name = name;
    }
}

