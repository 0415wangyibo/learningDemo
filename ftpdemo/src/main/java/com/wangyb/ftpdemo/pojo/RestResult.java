package com.wangyb.ftpdemo.pojo;

import lombok.Data;

/**
 * Created with Intellij IDEA.
 *
 * @author wangyb
 * @Date 2019/3/27 14:38
 * Modified By:
 * Description:
 */
@Data
public class RestResult<T> {

    public static final int SUCCESS = 0;
    public static final int CHECK_FAIL = 1;
    public static final int UNKNOWN_EXCEPTION = -99;

    private String msg;
    private int code;
    private T data;

    public RestResult() {
        super();
    }

    public RestResult(T data) {
        super();
        this.msg = "success";
        this.code = 0;
        this.data = data;
    }

    public RestResult(Throwable e) {
        super();
        this.msg = e.toString();
        this.code = UNKNOWN_EXCEPTION;
    }
}
