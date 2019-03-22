package com.example.demo.exception;

/**
 * Created with Intellij IDEA.
 *
 * @author wangyb
 * @Date 2019/1/7 14:44
 * Modified By:
 * Description:
 */
public class RequestParamErrorException extends Exception {

    private static final long serialVersionUID = 6029238338869481420L;

    public RequestParamErrorException(String message) {
        super(message);
    }
}
