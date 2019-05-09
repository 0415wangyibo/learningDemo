package com.wangyb.learningdemo.authentication.exception;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authz.AuthorizationException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wangyb.learningdemo.authentication.pojo.RestResult;
import lombok.extern.slf4j.Slf4j;
/**
 * Created with Intellij IDEA.
 *
 * @author wangyb
 * @Date 2018/10/8 16:32
 * Modified By:
 * Description:
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(value = BindException.class)
    @ResponseBody
    public RestResult<?> bindExceptionHandler(Exception e) {
        RestResult<?> result = new RestResult<Object>();
        result.setCode(RestResult.CHECK_FAIL);
        result.setMsg(e.getMessage());
        return result;
    }

    @ExceptionHandler(value = AuthenticationException.class)
    @ResponseBody
    public RestResult<?> LoginExceptionHandler(Exception e) {
        RestResult<?> result = new RestResult<Object>();
        result.setCode(RestResult.NO_LOGIN);
        result.setMsg(e.getMessage());
        return result;
    }

    @ExceptionHandler(value = AuthorizationException.class)
    @ResponseBody
    public RestResult<?> PermExceptionHandler(Exception e) {
        RestResult<?> result = new RestResult<Object>();
        result.setCode(RestResult.NO_PERMISSION);
        result.setMsg(e.getMessage());
        return result;
    }

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public RestResult<?> ExceptionHandler(Exception e) {
        log.error("by zhangyn: {}", e.getMessage(), e);
        RestResult<?> result = new RestResult<Object>();
        result.setCode(RestResult.UNKNOWN_EXCEPTION);
//		result.setMsg(e.toString());
        result.setMsg(e.getMessage());//返回具体的报错信息
        return result;
    }

}
