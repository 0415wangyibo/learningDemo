package com.wangyb.learningdemo.authentication.pojo;

import lombok.Data;
/**
 * Created with Intellij IDEA.
 *
 * @author wangyb
 * @Date 2018/10/8 16:36
 * Modified By:
 * Description:
 */

@Data
public class RestResult<T> {

    public static final int NO_LOGIN = -1;
    public static final int SUCCESS = 0;
    public static final int CHECK_FAIL = 1;
    public static final int NO_PERMISSION = 2;
    public static final int UNKNOWN_EXCEPTION = -99;

    private String msg = "success";
    private int code = SUCCESS;
    private T data;

    public RestResult() {
        super();
    }

    public RestResult(T data) {
        super();
        this.data = data;
    }

    public RestResult(Throwable e) {
        super();
        this.msg = e.toString();
        this.code = UNKNOWN_EXCEPTION;
    }

}