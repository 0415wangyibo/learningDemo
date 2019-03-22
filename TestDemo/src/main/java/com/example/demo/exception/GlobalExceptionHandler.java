package com.example.demo.exception;

import com.example.demo.pojo.RestResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MultipartException;

/**
 * Created with Intellij IDEA.
 *
 * @author wangyb
 * @Date 2019/1/7 14:42
 * Modified By:
 * Description:
 */
@RestControllerAdvice
@Component
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(value = BindException.class)
    public RestResult bindExceptionHandler(Exception e) {
        RestResult result = new RestResult<>();
        result.setCode(RestResult.CHECK_FAIL);
        result.setMsg(e.getMessage());
        return result;
    }

    @ExceptionHandler(value = MultipartException.class)
    public void multipartException(Exception e) {
        System.out.println("multipartException:" + e.getMessage());
    }

    //对参数异常进行捕获
    @ExceptionHandler(value = RequestParamErrorException.class)
    public RestResult paramExceptionHandler(Exception e) {
        RestResult result = new RestResult<>();
        result.setCode(RestResult.PARAM_FAIL);
        result.setMsg(e.getMessage());
        return result;
    }

    @ExceptionHandler(value = Exception.class)
    public RestResult exceptionHandler(Exception e) {
        log.error("by zhangyn: {}", e.getMessage(), e);
        RestResult result = new RestResult<>();
        result.setCode(RestResult.UNKNOWN_EXCEPTION);
        result.setMsg(e.getMessage());
        return result;
    }
}
