package com.potoyang.learn.mybatisredis;

import lombok.Data;

/**
 * Created with Intellij IDEA.
 *
 * @author potoyang
 * @since 2018/11/20 15:49
 * Modified:
 * Description:
 */
@Data
public class RestResult<T> {

    public static final int NO_LOGIN = -1;
    public static final int SUCCESS = 0;
    public static final int CHECK_FAIL = 1;
    public static final int NO_PERMISSION = 2;
    // ftp错误
    public static final int FTP_FAIL = 3;
    // 参数错误
    public static final int PARAM_FAIL = 4;
    public static final int UNKNOWN_EXCEPTION = -99;

    private String msg = "success";
    private int code = SUCCESS;
    private T data;

    public RestResult() {
    }

    public RestResult(T data) {
        this.code = SUCCESS;
        this.data = data;
    }

    public RestResult(Throwable e) {
        super();
        this.msg = e.toString();
        this.code = UNKNOWN_EXCEPTION;
    }

    public RestResult(String msg, int code, T data) {
        this.msg = msg;
        this.code = code;
        this.data = data;
    }
}
