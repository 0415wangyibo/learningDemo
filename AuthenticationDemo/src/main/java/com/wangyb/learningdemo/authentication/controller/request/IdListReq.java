package com.wangyb.learningdemo.authentication.controller.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * Created with Intellij IDEA.
 * 用于解决找不到list构造方法的问题
 * @author wangyb
 * @Date 2018/9/15 19:23
 * Modified By:
 * Description:防止出现没有List构造方法的异常，接收id列表参数
 */
@Data
public class IdListReq {
    @ApiModelProperty(value = "用于接收id列表")
    private List<Integer> list;

    public IdListReq(List<Integer> list) {
        this.list = list;
    }
}

