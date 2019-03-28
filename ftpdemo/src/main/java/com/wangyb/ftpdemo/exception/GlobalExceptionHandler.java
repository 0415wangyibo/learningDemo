package com.wangyb.ftpdemo.exception;

import com.wangyb.ftpdemo.pojo.RestResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created with Intellij IDEA.
 *
 * @author wangyb
 * @Date 2019/2/25 10:07
 * Modified By:
 * Description:
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(value = BindException.class)
    @ResponseBody
    public RestResult<?> bindExceptionHandler(Exception e) {
        RestResult<?> result = new RestResult<>();
        result.setCode(RestResult.CHECK_FAIL);
        result.setMsg(e.getMessage());
        return result;
    }

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public RestResult<?> ExceptionHandler(Exception e) {
        log.error("by wangyb: {}", e.getMessage(), e);
        RestResult<?> result = new RestResult<>();
        result.setCode(RestResult.UNKNOWN_EXCEPTION);
        result.setMsg(e.toString());
        return result;
    }
}
