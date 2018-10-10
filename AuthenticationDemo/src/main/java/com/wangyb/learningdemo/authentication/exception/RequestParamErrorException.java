package com.wangyb.learningdemo.authentication.exception;

/**
 * Created with Intellij IDEA.
 *
 * @author wangyb
 * @Date 2018/10/8 16:39
 * Modified By:
 * Description:
 */
public class RequestParamErrorException extends Exception {
    private static final long serialVersionUID = 6029238338869481420L;

    public RequestParamErrorException(String message) {
        super(message);
    }
}